package com.example.kardex_service.DTOs;

import com.example.kardex_service.Service.KardexService;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KardexCreateDTO {

    private KardexService.KardexMovementType type;

    private int affectedAmount;
    private String details;

    private Long clientId;
    private Long loanId;
    private Long toolId;
    private Long catalogId;
    private Long userId;
}
