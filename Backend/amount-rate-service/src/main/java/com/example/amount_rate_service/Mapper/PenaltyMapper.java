package com.example.amount_rate_service.Mapper;

import com.example.amount_rate_service.DTOs.PenaltyDTO;
import com.example.amount_rate_service.Entity.PenaltyEntity;
import com.example.amount_rate_service.Service.PenaltyService;

public class PenaltyMapper {

    public static PenaltyDTO toDto(PenaltyEntity entity) {
        PenaltyDTO dto = new PenaltyDTO();

        dto.setPenaltyId(entity.getPenaltyId());
        dto.setLoanId(entity.getLoanId());
        dto.setAmount(entity.getAmount());
        dto.setReason(entity.getReason());
        dto.setDelayDays(entity.getDelayDays());
        dto.setDailyFineRate(entity.getDailyFineRate());
        dto.setRepairCharge(entity.getRepairCharge());
        dto.setPenaltyStatus(entity.getPenaltyStatus().name());

        return dto;
    }

    public static PenaltyEntity toEntity(PenaltyDTO dto) {
        return PenaltyEntity.builder()
                .penaltyId(dto.getPenaltyId())
                .loanId(dto.getLoanId())
                .amount(dto.getAmount())
                .reason(dto.getReason())
                .delayDays(dto.getDelayDays())
                .penaltyStatus(
                        PenaltyService.PaymentStatus.valueOf(dto.getPenaltyStatus())
                )
                // config se asigna en el Service, no aquí
                .build();
    }
}

