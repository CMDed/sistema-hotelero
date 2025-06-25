package com.hotel.gestion.sistema_hotelero.controller;

import com.hotel.gestion.sistema_hotelero.model.Cliente;
import com.hotel.gestion.sistema_hotelero.model.Habitacion;
import com.hotel.gestion.sistema_hotelero.model.Reserva;
import com.hotel.gestion.sistema_hotelero.service.ClienteService;
import com.hotel.gestion.sistema_hotelero.service.HabitacionService;
import com.hotel.gestion.sistema_hotelero.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private final ClienteService clienteService;
    private final HabitacionService habitacionService;
    private final ReservaService reservaService;

    @Autowired
    public ReservaController(ClienteService clienteService, HabitacionService habitacionService, ReservaService reservaService) {
        this.clienteService = clienteService;
        this.habitacionService = habitacionService;
        this.reservaService = reservaService;
    }

    @GetMapping("/crear")
    public String showCrearReservaForm(Model model, @RequestParam(name = "dni", required = false) String dni) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("reserva", new Reserva());
        model.addAttribute("habitacionesDisponibles", habitacionService.obtenerHabitacionesDisponibles());

        if (dni != null && !dni.trim().isEmpty()) {
            Optional<Cliente> clienteOptional = clienteService.buscarClientePorDni(dni);
            if (clienteOptional.isPresent()) {
                model.addAttribute("clienteEncontrado", clienteOptional.get());
                model.addAttribute("cliente", clienteOptional.get());
            } else {
                model.addAttribute("errorMessage", "Cliente con DNI " + dni + " no encontrado.");
            }
        }
        return "reservas";
    }

    @PostMapping("/buscar-cliente")
    public String buscarClienteParaReserva(@RequestParam("dniBuscar") String dni, RedirectAttributes redirectAttributes) {
        Optional<Cliente> clienteOptional = clienteService.buscarClientePorDni(dni);
        if (clienteOptional.isPresent()) {
            redirectAttributes.addFlashAttribute("successMessage", "Cliente encontrado!");
            return "redirect:/reservas/crear?dni=" + dni;
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Cliente con DNI " + dni + " no encontrado. Por favor, regístrelo primero.");
            return "redirect:/reservas/crear";
        }
    }

    @GetMapping("/calcular-costo")
    @ResponseBody
    public String calcularCosto(
            @RequestParam("habitacionId") Long habitacionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        Optional<Habitacion> habitacionOptional = habitacionService.buscarHabitacionPorId(habitacionId);

        if (habitacionOptional.isEmpty()) {
            return "Error: Habitación no encontrada";
        }

        Habitacion habitacion = habitacionOptional.get();
        Integer dias = reservaService.calcularDiasEstadia(fechaInicio, fechaFin);
        Double total = reservaService.calcularTotalPagar(habitacion.getPrecioPorNoche(), dias);

        return String.format("{\"dias\": %d, \"total\": %.2f}", dias, total);
    }

    @PostMapping("/guardar")
    public String guardarReserva(@ModelAttribute Reserva reserva,
                                 @RequestParam("clienteDni") String clienteDni,
                                 @RequestParam("habitacionId") Long habitacionId,
                                 RedirectAttributes redirectAttributes) {
        try {
            Optional<Cliente> clienteOptional = clienteService.buscarClientePorDni(clienteDni);
            if (clienteOptional.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error: Cliente no encontrado para el DNI proporcionado.");
                return "redirect:/reservas/crear";
            }
            reserva.setCliente(clienteOptional.get());

            Optional<Habitacion> habitacionOptional = habitacionService.buscarHabitacionPorId(habitacionId);
            if (habitacionOptional.isEmpty() || !habitacionOptional.get().getEstado().equals("DISPONIBLE")) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error: Habitación no encontrada o no disponible.");
                return "redirect:/reservas/crear";
            }
            reserva.setHabitacion(habitacionOptional.get());

            Integer dias = reservaService.calcularDiasEstadia(reserva.getFechaInicio(), reserva.getFechaFin());
            Double total = reservaService.calcularTotalPagar(reserva.getHabitacion().getPrecioPorNoche(), dias);
            reserva.setDiasEstadia(dias);
            reserva.setTotalPagar(total);
            reserva.setEstadoReserva("ACTIVA");

            reservaService.guardarReserva(reserva);

            redirectAttributes.addFlashAttribute("successMessage", "Reserva creada exitosamente!");
            return "redirect:/reservas/crear";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear la reserva: " + e.getMessage());
            return "redirect:/reservas/crear";
        }
    }
}