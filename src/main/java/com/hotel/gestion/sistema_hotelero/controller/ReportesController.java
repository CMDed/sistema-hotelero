package com.hotel.gestion.sistema_hotelero.controller;

import com.hotel.gestion.sistema_hotelero.service.ReservaService;
import com.hotel.gestion.sistema_hotelero.service.HabitacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reportes")
public class ReportesController {

    private final ReservaService reservaService;
    private final HabitacionService habitacionService;

    @Autowired
    public ReportesController(ReservaService reservaService, HabitacionService habitacionService) {
        this.reservaService = reservaService;
        this.habitacionService = habitacionService;
    }

    @GetMapping("/generar")
    public String mostrarFormularioGenerarReporte(Model model) {
        model.addAttribute("fechaActual", LocalDate.now());
        model.addAttribute("currentPath", "/reportes/generar");
        return "generar_reporte";
    }

    @GetMapping("/api/ingresos")
    @ResponseBody
    public List<Map<String, Object>> getIngresosPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return reservaService.getIngresosPorPeriodo(fechaInicio, fechaFin);
    }

    /*@GetMapping("/api/ocupacion")
    @ResponseBody
    public List<Map<String, Object>> getOcupacionPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        long totalHabitaciones = habitacionService.contarTotalHabitaciones();
        return reservaService.getOcupacionPorPeriodo(fechaInicio, fechaFin, totalHabitaciones);
    }*/

    @GetMapping("/api/movimiento")
    @ResponseBody
    public List<Map<String, Object>> getMovimientoPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return reservaService.getMovimientoPorPeriodo(fechaInicio, fechaFin);
    }
}