package com.hotel.gestion.sistema_hotelero.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthLoginController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Hace referencia a src/main/resources/templates/login.html
    }

    @GetMapping("/dashboard")
    public String showDashboard() {
        // En un futuro, aquí podrías cargar datos para el dashboard según el rol del usuario
        return "dashboard"; // Hace referencia a src/main/resources/templates/dashboard.html
    }
}