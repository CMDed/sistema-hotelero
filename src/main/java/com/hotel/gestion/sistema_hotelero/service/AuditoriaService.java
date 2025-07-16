package com.hotel.gestion.sistema_hotelero.service;

import com.hotel.gestion.sistema_hotelero.model.Auditoria;
import com.hotel.gestion.sistema_hotelero.model.Empleado;
import com.hotel.gestion.sistema_hotelero.repository.AuditoriaRepository;
import com.hotel.gestion.sistema_hotelero.repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final EmpleadoRepository empleadoRepository;

    @Autowired
    public AuditoriaService(AuditoriaRepository auditoriaRepository, EmpleadoRepository empleadoRepository) {
        this.auditoriaRepository = auditoriaRepository;
        this.empleadoRepository = empleadoRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Auditoria registrarAccion(String tipoAccion, String detalleAccion, String entidadAfectada, Long entidadAfectadaId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = null;
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
            currentUsername = authentication.getName();
        } else if (authentication != null && authentication.isAuthenticated() && (authentication.getPrincipal() instanceof String)) {
            currentUsername = authentication.getName();
        }

        Empleado empleado = null;
        if (currentUsername != null && !currentUsername.equals("anonymousUser")) {
            Optional<Empleado> empleadoOptional = empleadoRepository.findByUsuarioUsername(currentUsername);
            empleado = empleadoOptional.orElse(null);
        }

        Auditoria logEntry = new Auditoria();
        logEntry.setTimestamp(LocalDateTime.now());
        logEntry.setEmpleado(empleado);
        logEntry.setTipoAccion(tipoAccion);
        logEntry.setDetalleAccion(detalleAccion);
        logEntry.setEntidadAfectada(entidadAfectada);
        logEntry.setEntidadAfectadaId(entidadAfectadaId);

        return auditoriaRepository.save(logEntry);
    }

    public List<Auditoria> obtenerTodosLosLogs() {
        return auditoriaRepository.findAll();
    }

    public List<Auditoria> obtenerLogsPorDniEmpleado(String dni) {
        return auditoriaRepository.findByEmpleadoDni(dni);
    }
}