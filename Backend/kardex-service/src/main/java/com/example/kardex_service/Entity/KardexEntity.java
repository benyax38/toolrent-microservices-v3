package com.example.kardex_service.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//JPA/Hibernate
@Entity //Mapea a una tabla en la base de datos
@Table(name = "kardexes")

//Lombok
@Data //Genera automaticamente getters, setters y metodos extra
@NoArgsConstructor //Genera un constructor vacio
@AllArgsConstructor //Genera un constructor con todos los atributos
@Builder //Permite ingresar los argumentos del constructor en cualquier orden

public class KardexEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movement_id")
    private Long movementId;

    @Column(name = "type", length = 20)
    private String type;

    @Column(name = "movement_date")
    private LocalDateTime movementDate;

    @Column(name = "affected_amount")
    private int affectedAmount;

    @Column(name = "details", length = 256)
    private String details;

    @Column(name = "client_id", nullable = true)
    private Long clientId;

    @Column(name = "loan_id", nullable = true)
    private Long loanId;

    @Column(name = "tool_id", nullable = true)
    private Long toolId;

    @Column(name = "tool_catalog_id", nullable = false)
    private Long toolCatalogId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

}
