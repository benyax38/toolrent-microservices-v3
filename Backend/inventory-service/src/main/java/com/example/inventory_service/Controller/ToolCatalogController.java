package com.example.inventory_service.Controller;

import com.example.inventory_service.DTOs.ToolCatalogAddUnitsDTO;
import com.example.inventory_service.DTOs.ToolCatalogCreateDTO;
import com.example.inventory_service.DTOs.ToolCatalogDTO;
import com.example.inventory_service.Service.ToolCatalogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogs")
public class ToolCatalogController {

    private final ToolCatalogService toolCatalogService;

    @Autowired
    public ToolCatalogController(ToolCatalogService toolCatalogService) {
        this.toolCatalogService = toolCatalogService;
    }

    @GetMapping
    public ResponseEntity<List<ToolCatalogDTO>> getAllCatalog() {
        List<ToolCatalogDTO> catalogList = toolCatalogService.getAllCatalogsDTO();
        return ResponseEntity.ok(catalogList);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ToolCatalogDTO> getCatalogById(@PathVariable Long id) {
        return ResponseEntity.ok(toolCatalogService.getCatalogsById(id));
    }

    @GetMapping("/name/{toolName}")
    public ResponseEntity<List<ToolCatalogDTO>> getCatalogByName(@PathVariable String toolName) {
        List<ToolCatalogDTO> results = toolCatalogService.getCatalogsByName(toolName);
        return ResponseEntity.ok(results);
    }

    @PostMapping
    public ResponseEntity<ToolCatalogDTO> createCatalog(
            @Valid @RequestBody ToolCatalogCreateDTO request,
            @RequestParam Long userId
    ) {
        ToolCatalogDTO createdCatalog =
                toolCatalogService.createCatalog(request, userId);

        return ResponseEntity.ok(createdCatalog);
    }

    @PostMapping("/{catalogId}/add-units")
    public ResponseEntity<ToolCatalogDTO> addUnitsToCatalog(
            @PathVariable Long catalogId,
            @RequestBody ToolCatalogAddUnitsDTO dto
    ) {
        ToolCatalogDTO catalog = toolCatalogService.addUnitsToCatalog(catalogId, dto);
        return ResponseEntity.ok(catalog);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCatalogById(@PathVariable Long id) {
        try {
            toolCatalogService.deleteCatalogsById(id);
            return ResponseEntity.ok("Herramienta eliminada del catálogo");
        } catch (IllegalArgumentException errorDeleteCatalogById) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDeleteCatalogById.getMessage());
        }
    }
}
