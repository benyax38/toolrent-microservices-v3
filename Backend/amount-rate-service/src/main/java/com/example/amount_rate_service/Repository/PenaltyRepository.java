package com.example.amount_rate_service.Repository;

import com.example.amount_rate_service.Entity.PenaltyEntity;
import com.example.amount_rate_service.Service.PenaltyService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Optional;

@Repository
public interface PenaltyRepository extends JpaRepository<PenaltyEntity,Long> {

    // Consulta que comprueba si el cliente del id ingresado tiene deudas sin pagar
    boolean existsByLoanIdAndPenaltyStatus(
            Long loanId,
            PenaltyService.PaymentStatus penaltyStatus
    );

    Optional<PenaltyEntity> findByLoanId(Long loanId);
}
