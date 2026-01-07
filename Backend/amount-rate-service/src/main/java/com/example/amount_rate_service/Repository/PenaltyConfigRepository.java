package com.example.amount_rate_service.Repository;

import com.example.amount_rate_service.Entity.PenaltyConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PenaltyConfigRepository extends JpaRepository<PenaltyConfigEntity,Long> {

    Optional<PenaltyConfigEntity> findTopByOrderByPenaltyConfigIdDesc();
}
