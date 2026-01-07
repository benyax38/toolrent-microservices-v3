package com.example.inventory_service.DTOs;

import com.example.inventory_service.Entity.ToolEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ToolDTO {

    private Long toolId;
    private String currentToolState;
    private Long toolCatalogId;
    private String toolCatalogName;

    public ToolDTO(ToolEntity tool) {
        this.toolId = tool.getToolId();
        this.currentToolState = tool.getCurrentToolState().name(); // si usas enum
        this.toolCatalogId = tool.getToolCatalogId();
        this.toolCatalogName = tool.getCurrentToolState().name();
    }
}

