package com.hotel.gestion.sistema_hotelero.controller;

import com.hotel.gestion.sistema_hotelero.model.Cliente;
import com.hotel.gestion.sistema_hotelero.model.Reserva;
import com.hotel.gestion.sistema_hotelero.service.ClienteService;
import com.hotel.gestion.sistema_hotelero.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
            if (clienteService.existeClientePorDni(cliente.getDni())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error: Ya existe un cliente con este DNI.");
                return "redirect:/clientes/registrar";
            }
            clienteService.guardarCliente(cliente);
            redirectAttributes.addFlashAttribute("successMessage", "Cliente registrado exitosamente.");
            return "redirect:/clientes/registrar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al registrar cliente: " + e.getMessage());
            return "redirect:/clientes/registrar";
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
}