package com.example.inventory_service.Entity;

import com.example.inventory_service.Service.ToolService;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tools")

// Lombok
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ToolEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tool_id")
    private Long toolId;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_tool_state", length = 20, nullable = false)
    private ToolService.ToolStatus currentToolState;

    /**
     * ID del catálogo (sin relación JPA)
     */
    @Column(name = "tool_catalog_id", nullable = false)
    private Long toolCatalogId;
}
