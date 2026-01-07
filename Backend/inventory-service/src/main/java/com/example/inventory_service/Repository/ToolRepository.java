package com.example.inventory_service.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolRepository extends JpaRepository<com.example.inventory_service.Entity.ToolEntity,Long> {
}

