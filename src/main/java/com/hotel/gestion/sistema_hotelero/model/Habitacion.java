package com.hotel.gestion.sistema_hotelero.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "habitaciones")
public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String numero;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(nullable = false)
    private Double precioPorNoche;

    @Column(nullable = false, length = 20)
    private String estado;

    public Habitacion(String numero, String tipo, Double precioPorNoche, String estado) {
        this.numero = numero;
        this.tipo = tipo;
        this.precioPorNoche = precioPorNoche;
        this.estado = estado;
    }

    public Habitacion(Long id) {
        this.id = id;
    }
}