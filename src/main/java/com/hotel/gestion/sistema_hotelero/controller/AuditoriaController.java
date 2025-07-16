package com.hotel.gestion.sistema_hotelero.controller;

import com.hotel.gestion.sistema_hotelero.model.Auditoria;
import com.hotel.gestion.sistema_hotelero.service.AuditoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/auditoria")
@PreAuthorize("hasRole('ADMIN')")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @Autowired
    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping("/logs")
    public String showLogsList(
            @RequestParam(name = "dniEmpleado", required = false) String dniEmpleado,
            Model model) {
        List<Auditoria> logs;
        if (dniEmpleado != null && !dniEmpleado.trim().isEmpty()) {
            logs = auditoriaService.obtenerLogsPorDniEmpleado(dniEmpleado.trim());
            model.addAttribute("filtroDni", dniEmpleado);
            if (logs.isEmpty()) {
                model.addAttribute("message", "No se encontraron logs para el DNI: " + dniEmpleado);
            }
        } else {
            logs = auditoriaService.obtenerTodosLosLogs();
        }
        model.addAttribute("logs", logs);
        return "listaLogs";
    }
}