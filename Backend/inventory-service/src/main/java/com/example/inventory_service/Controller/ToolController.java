package com.example.inventory_service.Controller;

import com.example.inventory_service.DTOs.ToolDTO;
import com.example.inventory_service.DTOs.ToolEvaluationDTO;
import com.example.inventory_service.Entity.ToolEntity;
import com.example.inventory_service.Service.ToolService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tools")
public class ToolController {

    private final ToolService toolService;

    @Autowired
    public ToolController(ToolService toolService) { this.toolService = toolService;
    }

    @GetMapping
    public ResponseEntity<List<ToolDTO>> getAllTool() {
        List<ToolDTO> toolList = toolService.getAllToolsDTO();
        return ResponseEntity.ok(toolList);
    }

    @PostMapping
    public ResponseEntity<ToolEntity> createTool(@Valid @RequestBody ToolEntity tool) {
        ToolEntity createdTool = toolService.createTools(tool);
        return ResponseEntity.ok(createdTool);
    }

    @PostMapping("/evaluation/{toolId}")
    public ResponseEntity<String> evaluateTool(
            @PathVariable Long toolId,
            @RequestBody ToolEvaluationDTO request
    ) {
        ToolService.ToolStatus newStatus = toolService.evaluateTool(toolId, request);

        return ResponseEntity.ok(
                "La herramienta #" + toolId +
                        " fue evaluada correctamente. Nuevo estado: " + newStatus
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteToolById(@PathVariable Long id) {
        try {
            toolService.deleteToolById(id);
            return ResponseEntity.ok("Herramienta eliminada correctamente");
        } catch (IllegalArgumentException errorDeleteToolById) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDeleteToolById.getMessage());
        }
    }
}
