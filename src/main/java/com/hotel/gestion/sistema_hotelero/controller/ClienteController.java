package com.hotel.gestion.sistema_hotelero.controller;

import com.hotel.gestion.sistema_hotelero.model.Cliente;
import com.hotel.gestion.sistema_hotelero.model.Reserva;
import com.hotel.gestion.sistema_hotelero.service.ClienteService;
import com.hotel.gestion.sistema_hotelero.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
                                 RedirectAttributes redirectAttributes) {

        if (cliente.getId() == null) {
            if (clienteService.existeClientePorDni(cliente.getDni())) {
                result.addError(new FieldError("cliente", "dni", cliente.getDni(), false, null, null, "El DNI ya está registrado."));
            }
        } else {
            Optional<Cliente> existingDniCliente = clienteService.buscarClientePorDni(cliente.getDni());
            if (existingDniCliente.isPresent() && !existingDniCliente.get().getId().equals(cliente.getId())) {
                result.addError(new FieldError("cliente", "dni", cliente.getDni(), false, null, null, "El DNI ya está registrado por otro cliente."));
            }
        }

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.cliente", result);
            redirectAttributes.addFlashAttribute("cliente", cliente);
            return (cliente.getId() == null) ? "redirect:/clientes/registrar" : "redirect:/clientes/editar/" + cliente.getId();
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
                return "redirect:/clientes/historial?id=" + clienteGuardado.getId();
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
            @RequestParam(name = "id", required = false) Long id,
            @RequestParam(name = "dni", required = false) String dni,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            Model model) {

        if (id != null) {
            Optional<Cliente> clienteOptional = clienteService.buscarClientePorId(id);
            if (clienteOptional.isPresent()) {
                Cliente cliente = clienteOptional.get();
                model.addAttribute("cliente", cliente);
                List<Reserva> reservas = reservaService.obtenerReservasPorCliente(cliente);
                model.addAttribute("reservasCliente", reservas);
            } else {
                model.addAttribute("errorMessage", "Cliente con ID " + id + " no encontrado.");
            }
        } else if (dni != null && !dni.trim().isEmpty()) {
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

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Cliente> clientesPage;
        if (search != null && !search.trim().isEmpty()) {
            clientesPage = clienteService.searchClientes(search, pageable);
        } else {
            clientesPage = clienteService.findAllClientes(pageable);
        }

        clientesPage.getContent().forEach(cliente -> {
            boolean hasActive = reservaService.tieneReservasActivas(cliente);
            cliente.setHasActiveReservations(hasActive);
        });

        model.addAttribute("clientesPage", clientesPage);
        model.addAttribute("currentPage", clientesPage.getNumber());
        model.addAttribute("totalPages", clientesPage.getTotalPages());
        model.addAttribute("totalItems", clientesPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("search", search);

        if (model.containsAttribute("errorMessageFlash")) {
            model.addAttribute("errorMessage", model.asMap().get("errorMessageFlash"));
        }
        if (model.containsAttribute("successMessageFlash")) {
            model.addAttribute("successMessage", model.asMap().get("successMessageFlash"));
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
            redirectAttributes.addFlashAttribute("errorMessageFlash", "Cliente no encontrado para editar.");
            return "redirect:/clientes/historial";
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            clienteService.eliminarCliente(id);
            redirectAttributes.addFlashAttribute("successMessageFlash", "Cliente eliminado exitosamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessageFlash", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessageFlash", "Error al eliminar el cliente: " + e.getMessage());
        }
        return "redirect:/clientes/historial";
    }
}