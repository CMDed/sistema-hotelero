package com.hotel.gestion.sistema_hotelero.controller;

import com.hotel.gestion.sistema_hotelero.model.Cliente;
import com.hotel.gestion.sistema_hotelero.model.Reserva;
import com.hotel.gestion.sistema_hotelero.service.ClienteService;
import com.hotel.gestion.sistema_hotelero.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final ReservaService reservaService;

    @Autowired
    public ClienteController(ClienteService clienteService, ReservaService reservaService) {
        this.clienteService = clienteService;
        this.reservaService = reservaService;
    }

    @GetMapping("/registrar")
    public String showRegistroClienteForm(Model model) {
        if (!model.containsAttribute("cliente")) {
            model.addAttribute("cliente", new Cliente());
        }
        return "registroCliente";
    }

    @PostMapping("/guardar")
    public String guardarCliente(@Valid @ModelAttribute("cliente") Cliente cliente,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {

        if (cliente.getId() == null) {
            Optional<Cliente> existingDni = clienteService.buscarClientePorDni(cliente.getDni());
            if (existingDni.isPresent()) {
                result.addError(new FieldError("cliente", "dni", cliente.getDni(), false, null, null, "El DNI ya está registrado."));
            }

        } else {
            Optional<Cliente> existingDni = clienteService.buscarClientePorDni(cliente.getDni());
            if (existingDni.isPresent() && !existingDni.get().getId().equals(cliente.getId())) {
                result.addError(new FieldError("cliente", "dni", cliente.getDni(), false, null, null, "El DNI ya está registrado por otro cliente."));
            }

        }

        if (result.hasErrors()) {
            if (cliente.getId() == null) {
                return "registroCliente";
            } else {
                return "editarCliente";
            }
        }

        try {
            Cliente clienteGuardado;
            if (cliente.getId() == null) {
                clienteGuardado = clienteService.crearCliente(cliente);
                redirectAttributes.addFlashAttribute("successMessage", "Cliente registrado exitosamente.");
                return "redirect:/clientes/registrar";
            } else {
                clienteGuardado = clienteService.actualizarCliente(cliente);
                redirectAttributes.addFlashAttribute("successMessage", "Cliente actualizado exitosamente.");
                return "redirect:/clientes/historial?dni=" + clienteGuardado.getDni();
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            if (cliente.getId() == null) {
                return "redirect:/clientes/registrar";
            } else {
                return "redirect:/clientes/editar/" + cliente.getId();
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar el cliente: " + e.getMessage());
            if (cliente.getId() == null) {
                return "redirect:/clientes/registrar";
            } else {
                return "redirect:/clientes/editar/" + cliente.getId();
            }
        }
    }

    @GetMapping("/historial")
    public String showHistorialClientePage(
            @RequestParam(name = "dni", required = false) String dni, Model model
    ) {
        if (dni != null && !dni.trim().isEmpty()) {
            Optional<Cliente> clienteOptional = clienteService.buscarClientePorDni(dni);
            if (clienteOptional.isPresent()) {
                Cliente cliente = clienteOptional.get();
                model.addAttribute("cliente", cliente);
                List<Reserva> reservas = reservaService.obtenerReservasPorCliente(cliente);
                model.addAttribute("reservasCliente", reservas);

            } else {
                model.addAttribute("errorMessage", "Cliente con DNI " + dni + " no encontrado.");
            }
        }
        if (!model.containsAttribute("reservasCliente")) {
            model.addAttribute("reservasCliente", List.of());
        }
        return "historialCliente";
    }

    @GetMapping("/editar/{id}")
    public String showEditarClienteForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Cliente> clienteOptional = clienteService.buscarClientePorId(id);
        if (clienteOptional.isPresent()) {
            model.addAttribute("cliente", clienteOptional.get());
            return "editarCliente";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Cliente no encontrado para editar.");
            return "redirect:/clientes/historial";
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            clienteService.eliminarCliente(id);
            redirectAttributes.addFlashAttribute("successMessage", "Cliente eliminado exitosamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el cliente: " + e.getMessage());
        }
        return "redirect:/clientes/historial";
    }
}