package com.hotel.gestion.sistema_hotelero.repository;

import com.hotel.gestion.sistema_hotelero.model.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
    List<Auditoria> findByEmpleadoDni(String dni);
}