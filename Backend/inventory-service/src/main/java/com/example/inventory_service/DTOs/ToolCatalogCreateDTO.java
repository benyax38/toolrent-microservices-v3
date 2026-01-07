package com.example.inventory_service.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ToolCatalogCreateDTO {

    @NotBlank(message = "El nombre de la herramienta es obligatorio")
    private String toolName;

    @NotBlank(message = "La categoría es obligatoria")
    private String toolCategory;

    @NotNull(message = "El valor diario de arriendo es obligatorio")
    @Positive(message = "El valor diario debe ser mayor que 0")
    private Double dailyRentalValue;

    @NotNull(message = "El valor de reposición es obligatorio")
    @Positive(message = "El valor de reposición debe ser mayor que 0")
    private Double replacementValue;

    private String description;

    @NotNull(message = "La cantidad disponible es obligatoria")
    @Positive(message = "La cantidad disponible debe ser mayor que 0")
    private Integer availableUnits;
}

