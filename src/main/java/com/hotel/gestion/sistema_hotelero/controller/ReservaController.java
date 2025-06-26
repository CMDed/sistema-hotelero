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

            reserva.setHabitacion(new Habitacion(habitacionId));

            if (reserva.getFechaInicio().isBefore(LocalDate.now())) {
                redirectAttributes.addFlashAttribute("errorMessage", "La fecha de inicio de la reserva no puede ser anterior a la fecha actual.");
                return "redirect:/reservas/crear";
            }

            if (reserva.getFechaInicio().isEqual(LocalDate.now())) {
                reserva.setEstadoReserva("ACTIVA");
            } else {
                reserva.setEstadoReserva("PENDIENTE");
            }

            reservaService.crearOActualizarReserva(reserva);

            redirectAttributes.addFlashAttribute("successMessage", "Reserva creada exitosamente!");
            return "redirect:/reservas/crear";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error de datos: " + e.getMessage());
            return "redirect:/reservas/crear";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error de configuración: " + e.getMessage());
            return "redirect:/reservas/crear";
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado al crear la reserva: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/reservas/crear";
        }
    }

    @PostMapping("/cancelar/{id}")
    public String cancelarReserva(@PathVariable Long id, RedirectAttributes redirectAttributes, @RequestHeader(value = "Referer", required = false) String referer) {
        if (reservaService.cancelarReserva(id)) {
            redirectAttributes.addFlashAttribute("successMessage", "Reserva cancelada exitosamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No se pudo cancelar la reserva.");
        }
        return "redirect:" + (referer != null ? referer : "/dashboard");
    }

    @PostMapping("/finalizar/{id}")
    public String finalizarReserva(@PathVariable Long id, RedirectAttributes redirectAttributes, @RequestHeader(value = "Referer", required = false) String referer) {
        if (reservaService.finalizarReserva(id)) {
            redirectAttributes.addFlashAttribute("successMessage", "Reserva finalizada (check-out) exitosamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No se pudo finalizar la reserva. Verifique su estado.");
        }
        return "redirect:" + (referer != null ? referer : "/dashboard");
    }
}