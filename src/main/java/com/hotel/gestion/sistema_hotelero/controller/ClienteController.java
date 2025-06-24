package com.hotel.gestion.sistema_hotelero.controller;

import com.hotel.gestion.sistema_hotelero.model.Cliente;
import com.hotel.gestion.sistema_hotelero.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/registrar")
    public String showRegistroClienteForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "registroCliente";
    }

    @PostMapping("/guardar")
    public String guardarCliente(@ModelAttribute("cliente") Cliente cliente, RedirectAttributes redirectAttributes) {
        clienteService.guardarCliente(cliente);
        redirectAttributes.addFlashAttribute("successMessage", "Cliente registrado exitosamente!");
        return "redirect:/clientes/registrar";
    }

    @GetMapping("/buscarPorDni")
    public String buscarClientePorDni(@RequestParam("dni") String dni, Model model, RedirectAttributes redirectAttributes) {
        Optional<Cliente> clienteOpt = clienteService.buscarClientePorDni(dni);
        if (clienteOpt.isPresent()) {
            model.addAttribute("clienteEncontrado", clienteOpt.get());
            return "resultadoBusquedaCliente";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Cliente con DNI " + dni + " no encontrado.");
            return "redirect:/reservas/crear";
        }
    }

    @GetMapping("/historial")
    public String showHistorialClientePage() {
        return "historialCliente";
    }

    @PostMapping("/historial/buscar")
    public String buscarHistorialClientePorDni(@RequestParam("dni") String dni, Model model, RedirectAttributes redirectAttributes) {
        Optional<Cliente> clienteOpt = clienteService.buscarClientePorDni(dni);
        if (clienteOpt.isPresent()) {
            model.addAttribute("cliente", clienteOpt.get());
            return "historialClienteDetalle";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Cliente con DNI " + dni + " no encontrado para el historial.");
            return "redirect:/clientes/historial";
        }
    }
}