package com.hotel.gestion.sistema_hotelero.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id")
    private Empleado empleado;

    @Column(nullable = false, length = 100)
    private String tipoAccion;

    @Column(nullable = false, length = 500)
    private String detalleAccion;

    @Column(length = 50)
    private String entidadAfectada;

    @Column
    private Long entidadAfectadaId;

    public Auditoria(LocalDateTime timestamp, Empleado empleado, String tipoAccion, String detalleAccion, String entidadAfectada, Long entidadAfectadaId) {
        this.timestamp = timestamp;
        this.empleado = empleado;
        this.tipoAccion = tipoAccion;
        this.detalleAccion = detalleAccion;
        this.entidadAfectada = entidadAfectada;
        this.entidadAfectadaId = entidadAfectadaId;
    }
}