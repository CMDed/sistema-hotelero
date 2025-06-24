package com.hotel.gestion.sistema_hotelero.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Muchas reservas pueden ser de un cliente
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY) // Muchas reservas pueden ser de una habitacion
    @JoinColumn(name = "habitacion_id", nullable = false)
    private Habitacion habitacion;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaSalida;

    @Column(nullable = false)
    private LocalDateTime horaEntrada; // Incluye fecha y hora
    
    @Column(nullable = false)
    private LocalDateTime horaSalida; // Incluye fecha y hora

    @Column(nullable = false)
    private Integer diasAPagar;

    @Column(nullable = false)
    private Double totalAPagar;

    // Podríamos añadir un estado de reserva: "PENDIENTE", "CONFIRMADA", "CANCELADA", "CHECK_IN", "CHECK_OUT"
    private String estadoReserva;

}