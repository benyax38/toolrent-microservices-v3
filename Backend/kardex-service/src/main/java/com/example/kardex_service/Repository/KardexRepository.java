package com.example.kardex_service.Repository;

import com.example.kardex_service.Entity.KardexEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KardexRepository extends JpaRepository<KardexEntity,Long> {
}
