package com.example.client_service.Controller;

import com.example.client_service.DTOs.ClientDTO;
import com.example.client_service.DTOs.ClientRequestDTO;
import com.example.client_service.DTOs.ClientStatusResponseDTO;
import com.example.client_service.Entity.ClientEntity;
import com.example.client_service.Service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    // 1. Obtener todos los clientes
    @GetMapping
    public ResponseEntity<List<ClientDTO>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    // 2. Crear cliente
    @PostMapping
    public ResponseEntity<ClientEntity> createClient(
            @RequestBody ClientRequestDTO request
    ) {
        ClientEntity created = clientService.createClients(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 3. Actualizar estado del cliente
    @PatchMapping("/{clientId}/estado")
    public ResponseEntity<ClientStatusResponseDTO> updateClientState(
            @PathVariable Long clientId,
            @Valid @RequestParam String newState) {

        ClientEntity updatedClient = clientService.updateClientStatus(clientId, newState);

        ClientStatusResponseDTO clientStatusResponseDTO = new ClientStatusResponseDTO(
                updatedClient.getClientId(),
                updatedClient.getClientFirstName(),
                updatedClient.getClientState().name()
        );

        return ResponseEntity.ok(clientStatusResponseDTO);
    }

    // 4. Validar que el cliente esté activo (endpoint CLAVE para microservicios)
    @GetMapping("/{id}/validate-active")
    public ResponseEntity<ClientDTO> validateClientIsActive(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                clientService.validateClientIsActive(id)
        );
    }

    // 5. Eliminar cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(
            @PathVariable Long id
    ) {
        clientService.deleteClientsById(id);
        return ResponseEntity.noContent().build();
    }
}

