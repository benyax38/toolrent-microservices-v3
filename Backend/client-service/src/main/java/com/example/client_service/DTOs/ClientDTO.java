package com.example.client_service.DTOs;

import com.example.client_service.Service.ClientService;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@JsonPropertyOrder({
        "clientId",
        "clientFirstName",
        "clientLastName",
        "clientRut",
        "clientPhone",
        "clientEmail"
})

/*
 * El objetivo de este DTO es mostrar la informacion de los clientes
 * en el endpoint que hace get all
 */
public class ClientDTO {

    private Long clientId;
    private String clientFirstName;
    private String clientLastName;
    private String clientRUT;
    private String clientPhone;
    private String clientEmail;
    private ClientService.ClientStatus clientState;
}
