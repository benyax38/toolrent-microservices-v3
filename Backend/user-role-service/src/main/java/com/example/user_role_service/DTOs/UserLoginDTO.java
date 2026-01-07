package com.example.user_role_service.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

// Se usa en el endpoint /login para validar credenciales
public class UserLoginDTO {

    private String userRut;
    private String userPassword;
}


