package com.hotel.gestion.sistema_hotelero.controller;

import com.hotel.gestion.sistema_hotelero.model.Empleado;
import com.hotel.gestion.sistema_hotelero.model.Usuario;
import com.hotel.gestion.sistema_hotelero.service.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    @Autowired
    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @GetMapping("/registrar")
    public String showRegistroEmpleadoForm(Model model) {
        if (!model.containsAttribute("empleado")) {
            Empleado empleado = new Empleado();
            empleado.setUsuario(new Usuario());
            model.addAttribute("empleado", empleado);
        }
        return "registrarEmpleado";
    }

    @PostMapping("/registrar")
    public String registrarEmpleado(@ModelAttribute Empleado empleado, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("Intentando registrar empleado:");
            System.out.println("Nombres: " + empleado.getNombres());
            System.out.println("DNI: " + empleado.getDni());
            System.out.println("Username: " + empleado.getUsuario().getUsername());

            Empleado nuevoEmpleado = empleadoService.registrarRecepcionista(empleado);
            redirectAttributes.addFlashAttribute("successMessage", "Recepcionista '" + nuevoEmpleado.getNombres() + " " + nuevoEmpleado.getApellidos() + "' registrado exitosamente!");
            return "redirect:/empleados/registrar";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("empleado", empleado); // Para repoblar el formulario
            return "redirect:/empleados/registrar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al registrar el recepcionista: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("empleado", empleado);
            return "redirect:/empleados/registrar";
        }
    }

    @GetMapping("/lista")
    public String listarEmpleados(Model model) {
        model.addAttribute("empleados", empleadoService.obtenerTodosLosEmpleados());
        if (model.asMap().containsKey("successMessage")) {
            model.addAttribute("successMessage", model.asMap().get("successMessage"));
        }
        if (model.asMap().containsKey("errorMessage")) {
            model.addAttribute("errorMessage", model.asMap().get("errorMessage"));
        }
        return "listaEmpleados";
    }

    @GetMapping("/editar/{id}")
    public String showEditarEmpleadoForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        if (!model.containsAttribute("empleado")) {
            Optional<Empleado> empleadoOptional = empleadoService.buscarEmpleadoPorId(id);
            if (empleadoOptional.isPresent()) {
                model.addAttribute("empleado", empleadoOptional.get());
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Empleado no encontrado para edición.");
                return "redirect:/empleados/lista";
            }
        }
        return "editarEmpleado";
    }

    @PostMapping("/actualizar")
    public String actualizarEmpleado(@ModelAttribute Empleado empleado, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("Intentando actualizar empleado con ID: " + empleado.getId());
            System.out.println("Nuevos Nombres: " + empleado.getNombres());
            System.out.println("Nuevo Username: " + empleado.getUsuario().getUsername());

            Empleado empleadoActualizado = empleadoService.actualizarEmpleado(empleado);

            redirectAttributes.addFlashAttribute("successMessage", "Empleado '" + empleadoActualizado.getNombres() + " " + empleadoActualizado.getApellidos() + "' actualizado exitosamente!");
            return "redirect:/empleados/lista";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("empleado", empleado);
            return "redirect:/empleados/editar/" + empleado.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar el empleado: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("empleado", empleado);
            return "redirect:/empleados/editar/" + empleado.getId();
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarEmpleado(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            if (empleadoService.eliminarEmpleado(id)) {
                redirectAttributes.addFlashAttribute("successMessage", "Empleado eliminado exitosamente.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "No se pudo eliminar el empleado. Es posible que no exista.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el empleado: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/empleados/lista";
    }
}