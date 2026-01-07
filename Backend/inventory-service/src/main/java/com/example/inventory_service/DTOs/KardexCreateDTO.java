package com.example.inventory_service.DTOs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KardexCreateDTO {
    private String type;          // INGRESO, EGRESO, AJUSTE
    private Integer affectedAmount;
    private String details;

    private Long clientId;
    private Long loanId;
    private Long toolId;
    private Long catalogId;
    private Long userId;
}

