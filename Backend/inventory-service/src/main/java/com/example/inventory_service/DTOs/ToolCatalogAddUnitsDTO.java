package com.example.inventory_service.DTOs;

import lombok.Data;

@Data
public class ToolCatalogAddUnitsDTO {
    private Integer units;
    private Long userId;
}
