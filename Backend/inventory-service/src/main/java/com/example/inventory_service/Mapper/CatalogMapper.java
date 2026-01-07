package com.example.inventory_service.Mapper;

import com.example.inventory_service.DTOs.ToolCatalogCreateDTO;
import com.example.inventory_service.DTOs.ToolCatalogDTO;
import com.example.inventory_service.Entity.ToolCatalogEntity;

public class CatalogMapper {

    // DTO de salida
    public static ToolCatalogDTO toDto(ToolCatalogEntity entity) {
        return ToolCatalogDTO.builder()
                .toolCatalogId(entity.getToolCatalogId())
                .toolName(entity.getToolName())
                .toolCategory(entity.getToolCategory())
                .dailyRentalValue(entity.getDailyRentalValue())
                .replacementValue(entity.getReplacementValue())
                .description(entity.getDescription())
                .availableUnits(entity.getAvailableUnits())
                .build();
    }

    // DTO de creación
    public static ToolCatalogEntity toEntity(ToolCatalogCreateDTO dto) {
        return ToolCatalogEntity.builder()
                .toolName(dto.getToolName())
                .toolCategory(dto.getToolCategory())
                .dailyRentalValue(dto.getDailyRentalValue())
                .replacementValue(dto.getReplacementValue())
                .description(dto.getDescription())
                .availableUnits(dto.getAvailableUnits())
                .build();
    }
}
