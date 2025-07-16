package com.hotel.gestion.sistema_hotelero.repository;

import com.hotel.gestion.sistema_hotelero.model.Auditoria;
import org.springframework.data.domain.Page; // Importar Page
import org.springframework.data.domain.Pageable; // Importar Pageable
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
    Page<Auditoria> findByEmpleadoDni(String dni, Pageable pageable);
    Page<Auditoria> findByTipoAccionContainingIgnoreCaseOrDetalleAccionContainingIgnoreCase(String tipoAccion, String detalleAccion, Pageable pageable);
}