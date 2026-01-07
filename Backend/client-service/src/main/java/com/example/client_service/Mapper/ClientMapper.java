package com.example.client_service.Mapper;

import com.example.client_service.DTOs.ClientDTO;
import com.example.client_service.Entity.ClientEntity;

public class ClientMapper {

    public static ClientDTO toDto(ClientEntity entity) {
        return ClientDTO.builder()
                .clientId(entity.getClientId())
                .clientFirstName(entity.getClientFirstName())
                .clientLastName(entity.getClientLastName())
                .clientRUT(entity.getClientRUT())
                .clientPhone(entity.getClientPhone())
                .clientEmail(entity.getClientEmail())
                .clientState(entity.getClientState())
                .build();
    }
}
