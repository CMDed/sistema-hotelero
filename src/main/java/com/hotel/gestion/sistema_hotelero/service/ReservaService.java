package com.hotel.gestion.sistema_hotelero.service;

import com.hotel.gestion.sistema_hotelero.model.Cliente;
import com.hotel.gestion.sistema_hotelero.model.Reserva;
import com.hotel.gestion.sistema_hotelero.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final HabitacionService habitacionService;

    @Autowired
    public ReservaService(ReservaRepository reservaRepository, HabitacionService habitacionService) {
        this.reservaRepository = reservaRepository;
        this.habitacionService = habitacionService;
    }

    public Reserva guardarReserva(Reserva reserva) {
        if (reserva.getHabitacion() != null && reserva.getHabitacion().getId() != null) {
            habitacionService.actualizarEstadoHabitacion(reserva.getHabitacion().getId(), "OCUPADA");
        } else {
            System.err.println("Advertencia: No se pudo actualizar el estado de la habitación porque su ID es nulo.");
        }
        return reservaRepository.save(reserva);
    }

    public Optional<Reserva> buscarReservaPorId(Long id) {
        return reservaRepository.findById(id);
    }

    public List<Reserva> obtenerTodasLasReservas() {
        return reservaRepository.findAll();
    }

    public Integer calcularDiasEstadia(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            return 0;
        }
        if (fechaFin.isBefore(fechaInicio)) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(fechaInicio, fechaFin);
    }

    public Double calcularTotalPagar(Double precioPorNoche, Integer diasEstadia) {
        if (precioPorNoche == null || diasEstadia == null) {
            return 0.0;
        }
        return precioPorNoche * diasEstadia;
    }

    public List<Reserva> obtenerReservasPorCliente(Cliente cliente) {
        if (cliente == null) {
            return List.of();
        }
        return reservaRepository.findByCliente(cliente);
    }
}