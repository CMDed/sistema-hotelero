package com.hotel.gestion.sistema_hotelero.controller;

import com.hotel.gestion.sistema_hotelero.model.Habitacion;
import com.hotel.gestion.sistema_hotelero.service.HabitacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/habitaciones")
public class HabitacionController {

    private final HabitacionService habitacionService;

    @Autowired
    public HabitacionController(HabitacionService habitacionService) {
        this.habitacionService = habitacionService;
    }

    @GetMapping
    public String listarHabitaciones(Model model) {
        model.addAttribute("habitaciones", habitacionService.obtenerTodasLasHabitaciones());
        return "habitaciones";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/nueva")
    public String mostrarFormularioCreacion(Model model) {
        model.addAttribute("habitacion", new Habitacion());
        model.addAttribute("accion", "nueva");
        return "habitacion-form";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/guardar")
    public String guardarHabitacion(@ModelAttribute("habitacion") Habitacion habitacion, RedirectAttributes redirectAttributes) {
        try {
            if (habitacion.getId() == null) {
                habitacionService.crearHabitacion(habitacion);
                redirectAttributes.addFlashAttribute("successMessage", "Habitación creada exitosamente.");
            } else {
                habitacionService.actualizarHabitacion(habitacion);
                redirectAttributes.addFlashAttribute("successMessage", "Habitación actualizada exitosamente.");
            }
            return "redirect:/habitaciones";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar la habitación: " + e.getMessage());
            return "redirect:/habitaciones/" + (habitacion.getId() == null ? "nueva" : "editar/" + habitacion.getId());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado al guardar la habitación: " + e.getMessage());
            return "redirect:/habitaciones/" + (habitacion.getId() == null ? "nueva" : "editar/" + habitacion.getId());
        }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Habitacion> habitacionOptional = habitacionService.buscarHabitacionPorId(id);
        if (habitacionOptional.isPresent()) {
            model.addAttribute("habitacion", habitacionOptional.get());
            model.addAttribute("accion", "editar");
            return "habitacion-form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Habitación no encontrada para editar.");
            return "redirect:/habitaciones";
        }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/eliminar")
    public String eliminarHabitacion(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            habitacionService.eliminarHabitacion(id);
            redirectAttributes.addFlashAttribute("successMessage", "Habitación eliminada exitosamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la habitación: " + e.getMessage());
        }
        return "redirect:/habitaciones";
    }
}