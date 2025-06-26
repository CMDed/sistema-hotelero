package com.hotel.gestion.sistema_hotelero.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "empleados")
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(nullable = false, unique = true, length = 20)
    private String dni;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = true, length = 20)
    private String telefono;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;

    public Empleado(String nombres, String apellidos, String dni, String email, String telefono, Usuario usuario) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.dni = dni;
        this.email = email;
        this.telefono = telefono;
        this.usuario = usuario;
    }
}