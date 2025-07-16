package com.hotel.gestion.sistema_hotelero.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$", message = "El nombre solo puede contener letras y espacios")
    @Column(nullable = false, length = 100)
    private String nombres;

    @NotBlank(message = "El apellido no puede estar vacío")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$", message = "El apellido solo puede contener letras y espacios")
    @Column(nullable = false, length = 100)
    private String apellidos;

    @NotBlank(message = "El DNI no puede estar vacío")
    @Size(min = 8, max = 8, message = "El DNI debe tener exactamente 8 dígitos")
    @Pattern(regexp = "^[0-9]{8}$", message = "El DNI solo puede contener 8 números")
    @Column(nullable = false, unique = true, length = 8)
    private String dni;

    @Size(max = 50, message = "La nacionalidad no puede exceder los 50 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s-]*$", message = "La nacionalidad solo puede contener letras, espacios y guiones")
    @Column(length = 50)
    private String nacionalidad;

    @Email(message = "Ingrese un formato de correo electrónico válido")
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres")
    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String telefono;
}