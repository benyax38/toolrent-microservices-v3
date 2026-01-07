package com.example.amount_rate_service.Controller;

import com.example.amount_rate_service.DTOs.PenaltyDTO;
import com.example.amount_rate_service.Service.PenaltyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/penalties")
public class PenaltyController {

    private final PenaltyService penaltyService;

    public PenaltyController(PenaltyService penaltyService) {
        this.penaltyService = penaltyService;
    }

    /**
     * Obtener la multa asociada a un préstamo
     */
    @GetMapping("/loan/{loanId}")
    public ResponseEntity<PenaltyDTO> getPenaltyByLoanId(
            @PathVariable Long loanId
    ) {
        PenaltyDTO dto = penaltyService.getPenaltyByLoanId(loanId);
        return ResponseEntity.ok(dto);
    }

    /**
     * Crear una nueva multa
     */
    @PostMapping
    public ResponseEntity<PenaltyDTO> createPenalty(
            @RequestBody PenaltyDTO dto
    ) {
        PenaltyDTO created = penaltyService.createPenalty(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Eliminar una multa por ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePenalty(
            @PathVariable Long id
    ) {
        penaltyService.deletePenaltyById(id);
        return ResponseEntity.noContent().build();
    }
}
