package com.example.amount_rate_service.Service;

import com.example.amount_rate_service.DTOs.PenaltyDTO;
import com.example.amount_rate_service.Entity.PenaltyEntity;
import com.example.amount_rate_service.Mapper.PenaltyMapper;
import com.example.amount_rate_service.Repository.PenaltyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PenaltyService {

    private final PenaltyRepository penaltyRepository;

    @Autowired
    public PenaltyService(PenaltyRepository penaltyRepository) {
        this.penaltyRepository = penaltyRepository;
    }

    public enum PaymentStatus {
        PAGADO,
        IMPAGO
    }

    public PenaltyDTO getPenaltyByLoanId(Long loanId) {
        PenaltyEntity entity = penaltyRepository.findByLoanId(loanId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No existe multa para el préstamo " + loanId
                ));

        return PenaltyMapper.toDto(entity);
    }


    public PenaltyDTO createPenalty(PenaltyDTO dto) {

        if (penaltyRepository.existsByLoanIdAndPenaltyStatus(
                dto.getLoanId(),
                PaymentStatus.IMPAGO
        )) {
            throw new IllegalStateException(
                    "El préstamo ya tiene una multa impaga"
            );
        }

        PenaltyEntity entity = PenaltyMapper.toEntity(dto);
        PenaltyEntity saved = penaltyRepository.save(entity);

        return PenaltyMapper.toDto(saved);
    }


    public void deletePenaltyById(Long id) {
        if (!penaltyRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "No existe multa con id: " + id
            );
        }
        penaltyRepository.deleteById(id);
    }
}

