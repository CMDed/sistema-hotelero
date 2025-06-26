package com.hotel.gestion.sistema_hotelero.controller;

import com.hotel.gestion.sistema_hotelero.service.HabitacionService;
import com.hotel.gestion.sistema_hotelero.service.ClienteService;
import com.hotel.gestion.sistema_hotelero.service.ReservaService;
import com.hotel.gestion.sistema_hotelero.service.EmpleadoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class DashboardController {

    private final HabitacionService habitacionService;
    private final ClienteService clienteService;
    private final ReservaService reservaService;
    private final EmpleadoService empleadoService;

    @Autowired
    public DashboardController(HabitacionService habitacionService, ClienteService clienteService, ReservaService reservaService, EmpleadoService empleadoService) {
        this.habitacionService = habitacionService;
        this.clienteService = clienteService;
        this.reservaService = reservaService;
        this.empleadoService = empleadoService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", authentication.getName());
        model.addAttribute("roles", authentication.getAuthorities());

        model.addAttribute("totalHabitaciones", habitacionService.contarTotalHabitaciones());
        model.addAttribute("habitacionesDisponibles", habitacionService.obtenerHabitacionesDisponibles().size());
        model.addAttribute("habitacionesOcupadas", habitacionService.obtenerHabitacionesOcupadas().size());
        model.addAttribute("habitacionesMantenimiento", habitacionService.obtenerHabitacionesEnMantenimiento().size());

        model.addAttribute("totalClientes", clienteService.contarTotalClientes());
        model.addAttribute("totalReservas", reservaService.contarTotalReservas());

        LocalDate today = LocalDate.now();
        model.addAttribute("checkInsHoy", reservaService.contarCheckInsHoy());
        model.addAttribute("checkOutsHoy", reservaService.contarCheckOutsHoy());
        model.addAttribute("reservasPendientes", reservaService.contarReservasPorEstado("PENDIENTE"));
        model.addAttribute("reservasActivas", reservaService.contarReservasPorEstado("ACTIVA"));

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            model.addAttribute("totalEmpleados", empleadoService.contarTodosLosEmpleados());
            Double ingresosTotales = reservaService.calcularIngresosTotales();
            model.addAttribute("ingresosTotales", ingresosTotales);
        } else {
            model.addAttribute("totalEmpleados", 0L);
            model.addAttribute("ingresosTotales", 0.0);
        }
        return "dashboard";
    }
}