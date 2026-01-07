package com.example.kardex_service.Service;

import com.example.kardex_service.DTOs.KardexCreateDTO;
import com.example.kardex_service.DTOs.KardexDTO;
import com.example.kardex_service.Entity.KardexEntity;
import com.example.kardex_service.Repository.KardexRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class KardexService {

    private final KardexRepository kardexRepository;

    @Autowired
    public KardexService(KardexRepository kardexRepository) {
        this.kardexRepository = kardexRepository;
    }

    public enum KardexMovementType {
        INGRESO,
        PRESTAMO,
        DEVOLUCION,
        BAJA,
        REPARACION
    }

    public List<KardexDTO> getAllKardexesDTO() {
        return kardexRepository.findAll().stream()
                .map(kardex -> new KardexDTO(
                        kardex.getMovementId(),
                        kardex.getType(),
                        kardex.getMovementDate(),
                        kardex.getAffectedAmount(),
                        kardex.getDetails(),
                        kardex.getClientId(),
                        kardex.getLoanId(),
                        kardex.getToolId(),
                        kardex.getToolCatalogId(),
                        kardex.getUserId()
                ))
                .toList();
    }

    public KardexEntity createKardexes(KardexEntity kardex) {
        return kardexRepository.save(kardex);
    }

    @Transactional
    public void registerMovement(KardexCreateDTO dto) {
        KardexEntity movement = new KardexEntity();

        movement.setType(dto.getType().name());
        movement.setMovementDate(LocalDateTime.now());
        movement.setAffectedAmount(dto.getAffectedAmount());
        movement.setDetails(dto.getDetails());

        // Guardamos solo los IDs
        movement.setClientId(dto.getClientId());
        movement.setLoanId(dto.getLoanId());
        movement.setToolId(dto.getToolId());
        movement.setToolCatalogId(dto.getCatalogId());
        movement.setUserId(dto.getUserId());

        kardexRepository.save(movement);
    }

    public void deleteKardexesById(Long id) {
        if (!kardexRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe kardex con id: " + id);
        }
        kardexRepository.deleteById(id);
    }
}

