package com.example.inventory_service.Service;

import com.example.inventory_service.DTOs.ToolCatalogAddUnitsDTO;
import com.example.inventory_service.DTOs.ToolCatalogCreateDTO;
import com.example.inventory_service.DTOs.ToolCatalogDTO;
import com.example.inventory_service.Entity.ToolCatalogEntity;
import com.example.inventory_service.Entity.ToolEntity;
import com.example.inventory_service.Mapper.CatalogMapper;
import com.example.inventory_service.Repository.ToolCatalogRepository;
import com.example.inventory_service.Repository.ToolRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToolCatalogService {

    private final ToolCatalogRepository toolCatalogRepository;
    private final ToolRepository toolRepository;

    @Autowired
    public ToolCatalogService(ToolCatalogRepository toolCatalogRepository, ToolRepository toolRepository) {
        this.toolCatalogRepository = toolCatalogRepository;
        this.toolRepository = toolRepository;
    }

    public List<ToolCatalogDTO> getAllCatalogsDTO() {
        return toolCatalogRepository.findAll().stream()
                .map(catalog -> new ToolCatalogDTO(
                        catalog.getToolCatalogId(),
                        catalog.getToolName(),
                        catalog.getToolCategory(),
                        catalog.getDailyRentalValue(),
                        catalog.getReplacementValue(),
                        catalog.getDescription(),
                        catalog.getAvailableUnits()
                ))
                .toList();
    }

    public ToolCatalogDTO getCatalogsById(Long id) {
        ToolCatalogEntity entity = toolCatalogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Catálogo no encontrado con id: " + id
                ));

        return CatalogMapper.toDto(entity);
    }

    public List<ToolCatalogDTO> getCatalogsByName(String toolName) {
        var entities = toolCatalogRepository.findByToolNameContainingIgnoreCase(toolName);
        return entities.stream()
                .map(CatalogMapper::toDto)
                .toList();
    }

    @Transactional
    public ToolCatalogDTO createCatalog(ToolCatalogCreateDTO request, Long userId) {

        ToolCatalogEntity catalog = CatalogMapper.toEntity(request);
        ToolCatalogEntity savedCatalog = toolCatalogRepository.save(catalog);

        for (int i = 0; i < savedCatalog.getAvailableUnits(); i++) {
            ToolEntity tool = ToolEntity.builder()
                    .toolCatalogId(savedCatalog.getToolCatalogId())
                    .currentToolState(ToolService.ToolStatus.DISPONIBLE)
                    .build();

            toolRepository.save(tool);
        }

        // Movimientos de kardex

        return CatalogMapper.toDto(savedCatalog);
    }

    @Transactional
    public ToolCatalogDTO addUnitsToCatalog(
            Long catalogId,
            ToolCatalogAddUnitsDTO dto
    ) {

        ToolCatalogEntity catalog = toolCatalogRepository.findById(catalogId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Catálogo no encontrado con id: " + catalogId
                ));

        if (dto.getUnits() == null || dto.getUnits() <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor a 0"
            );
        }

        // 1. Crear nuevas herramientas físicas
        for (int i = 0; i < dto.getUnits(); i++) {
            ToolEntity tool = new ToolEntity();
            tool.setToolCatalogId(catalog.getToolCatalogId()); // SOLO ID
            tool.setCurrentToolState(ToolService.ToolStatus.DISPONIBLE);
            toolRepository.save(tool);
        }

        // 2. Actualizar stock
        catalog.setAvailableUnits(
                catalog.getAvailableUnits() + dto.getUnits()
        );
        toolCatalogRepository.save(catalog);

        // 3. Notificar a Kardex (HTTP / Feign)

        return CatalogMapper.toDto(catalog);
    }

    @Transactional
    public void deleteCatalogsById(Long id) {

        ToolCatalogEntity catalog = toolCatalogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Catálogo no encontrado con id: " + id
                ));

        if (catalog.getAvailableUnits() > 0) {
            throw new IllegalStateException(
                    "No se puede eliminar un catálogo con herramientas activas"
            );
        }

        toolCatalogRepository.delete(catalog);
    }
}
