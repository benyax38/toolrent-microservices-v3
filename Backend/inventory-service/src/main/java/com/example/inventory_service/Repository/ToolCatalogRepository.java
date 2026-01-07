package com.example.inventory_service.Repository;

import com.example.inventory_service.Entity.ToolCatalogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ToolCatalogRepository extends JpaRepository<ToolCatalogEntity,Long> {

    List<ToolCatalogEntity> findByToolNameContainingIgnoreCase(String toolName);
}
