package com.example.user_role_service.Repository;

import com.example.user_role_service.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {

    // buscar por rut
    Optional<UserEntity> findByUserRut(String userRut);
}
