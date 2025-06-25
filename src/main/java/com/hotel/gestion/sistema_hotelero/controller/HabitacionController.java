package com.hotel.gestion.sistema_hotelero.controller;

import com.hotel.gestion.sistema_hotelero.model.Habitacion;
import com.hotel.gestion.sistema_hotelero.service.HabitacionService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/nueva")
    public String mostrarFormularioCreacion(Model model) {
        model.addAttribute("habitacion", new Habitacion());
        model.addAttribute("accion", "nueva");
        return "habitacion-form";
    }

    @PostMapping("/guardar")
    public String guardarHabitacion(@ModelAttribute("habitacion") Habitacion habitacion, RedirectAttributes redirectAttributes) {
        Optional<Habitacion> existingHabitacion = habitacionService.buscarHabitacionPorNumero(habitacion.getNumero());
        if (existingHabitacion.isPresent() && (habitacion.getId() == null || !existingHabitacion.get().getId().equals(habitacion.getId()))) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: Ya existe una habitación con el número " + habitacion.getNumero() + ".");
            return "redirect:/habitaciones/nueva";
        }

        habitacionService.guardarHabitacion(habitacion);
        redirectAttributes.addFlashAttribute("successMessage", "Habitación guardada exitosamente.");
        return "redirect:/habitaciones";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Habitacion> habitacionOptional = habitacionService.buscarHabitacionPorId(id);
        if (habitacionOptional.isPresent()) {
            model.addAttribute("habitacion", habitacionOptional.get());
            model.addAttribute("accion", "editar");
            return "habitacion-form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Habitación no encontrada.");
            return "redirect:/habitaciones";
        }
    }

    @PostMapping("/eliminar")
    public String eliminarHabitacion(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<Habitacion> habitacionOptional = habitacionService.buscarHabitacionPorId(id);
        if (habitacionOptional.isPresent()) {
            habitacionService.eliminarHabitacion(id);
            redirectAttributes.addFlashAttribute("successMessage", "Habitación eliminada exitosamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: Habitación no encontrada para eliminar.");
        }
        return "redirect:/habitaciones";
    }
}