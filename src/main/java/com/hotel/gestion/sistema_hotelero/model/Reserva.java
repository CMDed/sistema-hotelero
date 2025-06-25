package com.hotel.gestion.sistema_hotelero.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habitacion_id", nullable = false)
    private Habitacion habitacion;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false)
    private LocalTime horaEntrada;

    @Column(nullable = false)
    private LocalTime horaSalida;

    @Column(nullable = false)
    private Integer diasEstadia;

    @Column(nullable = false)
    private Double totalPagar;

    @Column(nullable = false, length = 20)
    private String estadoReserva;

    public Reserva(Cliente cliente, Habitacion habitacion, LocalDate fechaInicio, LocalDate fechaFin, LocalTime horaEntrada, LocalTime horaSalida, Integer diasEstadia, Double totalPagar, String estadoReserva) {
        this.cliente = cliente;
        this.habitacion = habitacion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
        this.diasEstadia = diasEstadia;
        this.totalPagar = totalPagar;
        this.estadoReserva = estadoReserva;
    }
}