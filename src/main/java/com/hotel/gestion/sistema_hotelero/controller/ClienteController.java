package com.hotel.gestion.sistema_hotelero.controller;

import com.hotel.gestion.sistema_hotelero.model.Cliente;
import com.hotel.gestion.sistema_hotelero.model.Reserva;
import com.hotel.gestion.sistema_hotelero.service.ClienteService;
import com.hotel.gestion.sistema_hotelero.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
        model.addAttribute("cliente", new Cliente());
        return "registroCliente";
    }

    @PostMapping("/guardar")
    public String guardarCliente(@ModelAttribute("cliente") Cliente cliente, RedirectAttributes redirectAttributes) {
        try {
            boolean isNewClient = (cliente.getId() == null);

            if (isNewClient) {
                if (clienteService.existeClientePorDni(cliente.getDni())) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Error: Ya existe un cliente con este DNI.");
                    return "redirect:/clientes/registrar";
                }
            } else {
                Optional<Cliente> existingClientWithDni = clienteService.buscarClientePorDni(cliente.getDni());
                if (existingClientWithDni.isPresent() && !existingClientWithDni.get().getId().equals(cliente.getId())) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Error: El DNI proporcionado ya pertenece a otro cliente.");
                    return "redirect:/clientes/editar/" + cliente.getId();
                }
            }

            clienteService.guardarCliente(cliente);
            redirectAttributes.addFlashAttribute("successMessage", "Cliente " + (isNewClient ? "registrado" : "actualizado") + " exitosamente.");

            if (isNewClient) {
                return "redirect:/clientes/registrar";
            } else {
                return "redirect:/clientes/historial?dni=" + cliente.getDni();
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar cliente: " + e.getMessage());
            if (cliente.getId() != null) {
                return "redirect:/clientes/editar/" + cliente.getId();
            } else {
                return "redirect:/clientes/registrar";
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
                model.addAttribute("clienteEncontrado", cliente);

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

    @PostMapping("/cancelar-reserva")
    public String cancelarReserva(@RequestParam("reservaId") Long reservaId,
                                  @RequestParam("clienteDni") String clienteDni,
                                  RedirectAttributes redirectAttributes) {

        boolean cancelado = reservaService.cancelarReserva(reservaId);

        if (cancelado) {
            redirectAttributes.addFlashAttribute("successMessage", "Reserva cancelada y habitación liberada exitosamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cancelar la reserva. Es posible que no exista o no sea cancelable.");
        }

        return "redirect:/clientes/historial?dni=" + clienteDni;
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
}