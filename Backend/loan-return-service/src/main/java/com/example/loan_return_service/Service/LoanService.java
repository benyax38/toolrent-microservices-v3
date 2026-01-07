package com.example.loan_return_service.services;

import com.example.loan_return_service.Entity.LoanEntity;
import com.example.loanservice.entities.LoanEntity;
import com.example.loanservice.repositories.LoanRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final RestTemplate restTemplate;

    @Value("${services.client.base-url}")
    private String clientServiceUrl;

    @Value("${services.tool.base-url}")
    private String toolServiceUrl;

    @Value("${services.kardex.base-url}")
    private String kardexServiceUrl;

    @Value("${services.penalty.base-url}")
    private String penaltyServiceUrl;

    public enum LoanStatus { ACTIVO, VENCIDO, POR_PAGAR, FINALIZADO }
    public enum DamageLevel { NINGUNO, LEVE, GRAVE }

    // --- TAREA PROGRAMADA ---
    @Scheduled(cron = "0 0 0 * * *")
    public void checkAndUpdateLoanStatus() {
        List<LoanEntity> overdue = loanRepository.findByLoanStatusAndReturnDateIsNullAndDeadlineBefore(
                LoanStatus.ACTIVO, LocalDateTime.now());

        for (LoanEntity loan : overdue) {
            loan.setLoanStatus(LoanStatus.VENCIDO);
            loanRepository.save(loan);
            // Notificar al microservicio de clientes para restringir
            updateClientStatus(loan.getClientId(), "RESTRINGIDO", null);
        }
    }

    // --- CREAR PRÉSTAMO ---
    @Transactional
    public LoanEntity createLoan(Long clientId, Long toolId, LocalDateTime deadline, JwtAuthenticationToken principal) {
        // 1. Validar y Obtener Cliente (REST)
        ClientDTO client = getClient(clientId, principal);
        validateClientForLoan(client, principal);

        // 2. Validar y Reservar Herramienta (REST)
        ToolDTO tool = getTool(toolId, principal);
        if (!"DISPONIBLE".equals(tool.getCurrentToolState())) throw new RuntimeException("Herramienta no disponible");

        // 3. Lógica de negocio local
        if (deadline != null && deadline.isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("Deadline inválido");

        LoanEntity loan = LoanEntity.builder()
                .deliveryDate(LocalDateTime.now())
                .deadline(deadline)
                .loanStatus(LoanStatus.ACTIVO)
                .clientId(clientId)
                .toolId(toolId)
                .userId(extractUserId(principal))
                .build();

        LoanEntity savedLoan = loanRepository.save(loan);

        // 4. Actualizar estado de herramienta y Kardex (REST)
        updateToolStatus(toolId, "PRESTADA", principal);
        sendToKardex(savedLoan, "PRESTAMO", 1, "Herramienta prestada", principal);

        return savedLoan;
    }

    // --- PROCESAR DEVOLUCIÓN ---
    @Transactional
    public LoanEntity returnLoan(Long loanId, String damageLevelStr, JwtAuthenticationToken principal) {
        LoanEntity loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        DamageLevel damageLevel = parseDamageLevel(damageLevelStr);
        LocalDateTime now = LocalDateTime.now();
        boolean late = now.isAfter(loan.getDeadline());

        // Obtener configuración de multas y datos de catálogo (REST)
        PenaltyConfigDTO config = getPenaltyConfig(principal);
        ToolDTO tool = getTool(loan.getToolId(), principal);

        double rentalAmount = calculateRentalAmount(loan, now, late, damageLevel, config, tool, principal);

        // Actualizar Entidad Local
        loan.setReturnDate(now);
        loan.setLoanStatus(LoanStatus.POR_PAGAR);
        loan.setRentalAmount(rentalAmount);

        // Actualizar Cliente y Herramienta (REST)
        updateClientStatus(loan.getClientId(), "RESTRINGIDO", principal);
        String finalToolStatus = (damageLevel == DamageLevel.NINGUNO) ? "DISPONIBLE" : "EN_REPARACION";
        updateToolStatus(loan.getToolId(), finalToolStatus, principal);

        // Registrar en Kardex
        sendToKardex(loan, "DEVOLUCION", 1, "Devolución nivel: " + damageLevel, principal);

        return loanRepository.save(loan);
    }

    // --- PROCESAR PAGO ---
    @Transactional
    public LoanEntity payLoan(Long loanId, Double amountPaid, JwtAuthenticationToken principal) {
        LoanEntity loan = loanRepository.findById(loanId).orElseThrow();

        if (amountPaid < loan.getRentalAmount()) throw new RuntimeException("Monto insuficiente");

        // Marcar multa como pagada si existe (REST)
        markPenaltyAsPaid(loanId, principal);

        loan.setLoanStatus(LoanStatus.FINALIZADO);
        LoanEntity saved = loanRepository.save(loan);

        // Verificar si el cliente puede volver a estar ACTIVO (REST + Local)
        checkAndRestoreClientStatus(loan.getClientId(), principal);

        return saved;
    }

    // ==========================================
    // MÉTODOS DE APOYO Y COMUNICACIÓN REST
    // ==========================================

    private double calculateRentalAmount(LoanEntity loan, LocalDateTime now, boolean late,
                                         DamageLevel damage, PenaltyConfigDTO config,
                                         ToolDTO tool, JwtAuthenticationToken principal) {
        double rentalValue = tool.getDailyRentalValue();
        long days = Math.max(1, ChronoUnit.DAYS.between(loan.getDeliveryDate(), late ? loan.getDeadline() : now));
        double baseAmount = rentalValue * days;

        double extraCharges = 0;
        if (late) {
            int delayDays = (int) ChronoUnit.DAYS.between(loan.getDeadline(), now);
            extraCharges += (delayDays * config.getDailyFineRate());
            createPenalty(loan.getLoanId(), extraCharges, "Atraso", principal);
        }

        if (damage == DamageLevel.LEVE) {
            extraCharges += config.getRepairCharge();
            createPenalty(loan.getLoanId(), config.getRepairCharge(), "Daño Leve", principal);
        } else if (damage == DamageLevel.GRAVE) {
            extraCharges += tool.getReplacementValue();
            createPenalty(loan.getLoanId(), tool.getReplacementValue(), "Daño Grave", principal);
        }

        return baseAmount + extraCharges;
    }

    private void updateClientStatus(Long clientId, String status, JwtAuthenticationToken principal) {
        String url = UriComponentsBuilder.fromHttpUrl(clientServiceUrl + "/api/v1/clients/" + clientId + "/status")
                .queryParam("status", status).toUriString();
        restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(authHeaders(principal)), Void.class);
    }

    private void createPenalty(Long loanId, double amount, String reason, JwtAuthenticationToken principal) {
        PenaltyRequest req = new PenaltyRequest(loanId, amount, reason);
        restTemplate.postForEntity(penaltyServiceUrl + "/api/v1/penalties", new HttpEntity<>(req, authHeaders(principal)), Void.class);
    }

    private ToolDTO getTool(Long toolId, JwtAuthenticationToken principal) {
        return restTemplate.exchange(toolServiceUrl + "/api/v1/tools/" + toolId,
                HttpMethod.GET, new HttpEntity<>(authHeaders(principal)), ToolDTO.class).getBody();
    }

    private void sendToKardex(LoanEntity loan, String type, int qty, String details, JwtAuthenticationToken principal) {
        KardexRequest req = KardexRequest.builder()
                .type(type).affectedAmount(qty).details(details)
                .loanId(loan.getLoanId()).clientId(loan.getClientId()).toolId(loan.getToolId())
                .build();
        restTemplate.postForEntity(kardexServiceUrl + "/api/v1/kardex", new HttpEntity<>(req, authHeaders(principal)), Void.class);
    }

    private HttpHeaders authHeaders(JwtAuthenticationToken principal) {
        HttpHeaders headers = new HttpHeaders();
        if (principal != null) headers.setBearerAuth(principal.getToken().getTokenValue());
        return headers;
    }

    private Long extractUserId(JwtAuthenticationToken principal) {
        return Long.parseLong(principal.getTokenAttributes().get("sub").toString());
    }

    private DamageLevel parseDamageLevel(String level) {
        try { return DamageLevel.valueOf(level.toUpperCase()); }
        catch (Exception e) { return DamageLevel.NINGUNO; }
    }

    // --- DTOs INTERNOS ---
    @Data static class ClientDTO { private Long clientId; private String clientState; }
    @Data static class ToolDTO { private Long toolId; private String currentToolState; private double dailyRentalValue; private double replacementValue; }
    @Data static class PenaltyConfigDTO { private double dailyFineRate; private double repairCharge; }
    @Data @RequiredArgsConstructor static class PenaltyRequest { final Long loanId; final double amount; final String reason; }
    @Data @lombok.Builder static class KardexRequest { String type; int affectedAmount; String details; Long loanId; Long clientId; Long toolId; }
}