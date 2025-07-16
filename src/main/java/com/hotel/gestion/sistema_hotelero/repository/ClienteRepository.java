package com.hotel.gestion.sistema_hotelero.repository;

import com.hotel.gestion.sistema_hotelero.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByDni(String dni);
    Optional<Cliente> findByEmail(String email);
    Page<Cliente> findByDniContainingIgnoreCaseOrNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(String dni, String nombres, String apellidos, Pageable pageable);
}