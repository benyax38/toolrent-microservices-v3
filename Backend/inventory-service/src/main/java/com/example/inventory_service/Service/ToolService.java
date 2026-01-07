package com.example.inventory_service.Service;

import com.example.inventory_service.DTOs.ToolDTO;
import com.example.inventory_service.DTOs.ToolEvaluationDTO;
import com.example.inventory_service.Entity.ToolCatalogEntity;
import com.example.inventory_service.Entity.ToolEntity;
import com.example.inventory_service.Repository.ToolCatalogRepository;
import com.example.inventory_service.Repository.ToolRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ToolService {

    private final ToolRepository toolRepository;
    private final ToolCatalogRepository toolCatalogRepository;

    @Autowired
    public ToolService(ToolRepository toolRepository, ToolCatalogRepository toolCatalogRepository) {
        this.toolRepository = toolRepository;
        this.toolCatalogRepository = toolCatalogRepository;
    }

    //Se definen los estados posibles de una herramienta
    public enum ToolStatus {
        DISPONIBLE,
        PRESTADA,
        EN_REPARACION,
        DADA_DE_BAJA
    }

    // Se definen las posibles decisiones de evaluacion de daño de una herramienta
    public enum ToolEvaluationDecision {
        DAR_DE_BAJA,
        REPARADA
    }

    @Transactional
    public void validateAndLoanTool(Long toolId) {

        // 1. Buscar herramienta
        ToolEntity tool = toolRepository.findById(toolId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Herramienta no encontrada con id: " + toolId
                ));

        // 2. Validar estado
        if (tool.getCurrentToolState() != ToolStatus.DISPONIBLE) {
            throw new IllegalStateException(
                    "La herramienta no está disponible para préstamo"
            );
        }

        // 3. Validar catálogo asociado
        Long catalogId = tool.getToolCatalogId();
        if (catalogId == null) {
            throw new IllegalStateException(
                    "La herramienta no tiene catálogo asociado"
            );
        }

        ToolCatalogEntity catalog = toolCatalogRepository.findById(catalogId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Catálogo no encontrado con id: " + catalogId
                ));

        // 4. Validar stock
        if (catalog.getAvailableUnits() <= 0) {
            throw new IllegalStateException(
                    "No hay stock disponible para el catálogo"
            );
        }

        // 5. Actualizar estados
        tool.setCurrentToolState(ToolStatus.PRESTADA);
        catalog.setAvailableUnits(
                catalog.getAvailableUnits() - 1
        );

        // 6. Persistir
        toolRepository.save(tool);
        toolCatalogRepository.save(catalog);
    }


    @Transactional
    public ToolStatus evaluateTool(Long toolId, ToolEvaluationDTO dto) {

        ToolEntity tool = toolRepository.findById(toolId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Herramienta no encontrada con id: " + toolId
                ));

        if (tool.getCurrentToolState() != ToolStatus.EN_REPARACION) {
            throw new IllegalStateException(
                    "La herramienta no está en estado EN_REPARACION"
            );
        }

        Long catalogId = tool.getToolCatalogId();
        ToolCatalogEntity catalog = toolCatalogRepository.findById(catalogId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Catálogo no encontrado con id: " + catalogId
                ));

        switch (dto.getDecision()) {

            case DAR_DE_BAJA -> {
                tool.setCurrentToolState(ToolStatus.DADA_DE_BAJA);
            }

            case REPARADA -> {
                tool.setCurrentToolState(ToolStatus.DISPONIBLE);
                catalog.setAvailableUnits(catalog.getAvailableUnits() + 1);
                toolCatalogRepository.save(catalog);
            }

            default -> throw new IllegalArgumentException(
                    "Decisión inválida: " + dto.getDecision()
            );
        }

        toolRepository.save(tool);
        return tool.getCurrentToolState();
    }


    public List<ToolDTO> getAllToolsDTO() {
        List<ToolEntity> tools = toolRepository.findAll();
        return tools.stream()
                .map(ToolDTO::new) // usa el constructor que mapea desde la entidad
                .collect(Collectors.toList());
    }

    public ToolEntity createTools(ToolEntity tool) {
        tool.setCurrentToolState(ToolStatus.DISPONIBLE);
        return toolRepository.save(tool);
    }

    public void deleteToolById(Long toolId) {

        ToolEntity tool = toolRepository.findById(toolId)
                .orElseThrow(() -> new RuntimeException("Herramienta no encontrada"));

        if (tool.getCurrentToolState() == ToolStatus.PRESTADA) {
            throw new RuntimeException("No se puede eliminar una herramienta prestada");
        }

        tool.setCurrentToolState(ToolStatus.DADA_DE_BAJA);
        toolRepository.save(tool);
    }
}
