package com.hotel.gestion.sistema_hotelero.controller;

import com.hotel.gestion.sistema_hotelero.service.HabitacionService;
import com.hotel.gestion.sistema_hotelero.service.ClienteService;
import com.hotel.gestion.sistema_hotelero.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final HabitacionService habitacionService;
    private final ClienteService clienteService;
    private final ReservaService reservaService;

    @Autowired
    public DashboardController(HabitacionService habitacionService, ClienteService clienteService, ReservaService reservaService) {
        this.habitacionService = habitacionService;
        this.clienteService = clienteService;
        this.reservaService = reservaService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", authentication.getName());
        model.addAttribute("roles", authentication.getAuthorities());

        long totalHabitaciones = habitacionService.contarTotalHabitaciones();
        long habitacionesDisponibles = habitacionService.obtenerHabitacionesDisponibles().size();
        long habitacionesOcupadas = habitacionService.obtenerHabitacionesOcupadas().size();
        long habitacionesMantenimiento = habitacionService.obtenerHabitacionesEnMantenimiento().size();

        long totalClientes = clienteService.contarTotalClientes();
        long totalReservas = reservaService.contarTotalReservas();
        long checkInsHoy = reservaService.contarCheckInsHoy();
        long checkOutsHoy = reservaService.contarCheckOutsHoy();
        long reservasPendientes = reservaService.contarReservasPorEstado("PENDIENTE");
        long reservasActivas = reservaService.contarReservasPorEstado("ACTIVA");

        model.addAttribute("totalHabitaciones", totalHabitaciones);
        model.addAttribute("habitacionesDisponibles", habitacionesDisponibles);
        model.addAttribute("habitacionesOcupadas", habitacionesOcupadas);
        model.addAttribute("habitacionesMantenimiento", habitacionesMantenimiento);

        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("totalReservas", totalReservas);
        model.addAttribute("checkInsHoy", checkInsHoy);
        model.addAttribute("checkOutsHoy", checkOutsHoy);
        model.addAttribute("reservasPendientes", reservasPendientes);
        model.addAttribute("reservasActivas", reservasActivas);

        Double ingresosTotales = reservaService.calcularIngresosTotales();
        model.addAttribute("ingresosTotales", ingresosTotales);

        return "dashboard";
    }
}