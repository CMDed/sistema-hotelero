package com.hotel.gestion.sistema_hotelero.controller;

import com.hotel.gestion.sistema_hotelero.model.Auditoria;
import com.hotel.gestion.sistema_hotelero.service.AuditoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            Model model) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Auditoria> logsPage;

        if (dniEmpleado != null && !dniEmpleado.trim().isEmpty()) {
            logsPage = auditoriaService.obtenerLogsPorDniEmpleado(dniEmpleado.trim(), pageable);
            model.addAttribute("filtroDni", dniEmpleado);
            if (logsPage.isEmpty()) {
                model.addAttribute("message", "No se encontraron logs para el DNI: " + dniEmpleado + " en la página actual.");
            }
        } else if (search != null && !search.trim().isEmpty()) {
            logsPage = auditoriaService.searchLogs(search.trim(), pageable);
            model.addAttribute("search", search);
            if (logsPage.isEmpty()) {
                model.addAttribute("message", "No se encontraron logs que coincidan con '" + search + "' en la página actual.");
            }
        } else {
            logsPage = auditoriaService.obtenerTodosLosLogs(pageable);
        }

        model.addAttribute("logsPage", logsPage);
        model.addAttribute("currentPage", logsPage.getNumber());
        model.addAttribute("totalPages", logsPage.getTotalPages());
        model.addAttribute("totalItems", logsPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);

        return "listaLogs";
    }
}