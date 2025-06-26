package com.hotel.gestion.sistema_hotelero.service;

import com.hotel.gestion.sistema_hotelero.model.Cliente;
import com.hotel.gestion.sistema_hotelero.model.Habitacion;
import com.hotel.gestion.sistema_hotelero.model.Reserva;
import com.hotel.gestion.sistema_hotelero.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Reserva guardarReserva(Reserva reserva) {
        Reserva savedReserva = reservaRepository.save(reserva);

        if (savedReserva.getHabitacion() != null && savedReserva.getHabitacion().getId() != null) {
            if ("ACTIVA".equals(savedReserva.getEstadoReserva())) {
                habitacionService.actualizarEstadoHabitacion(savedReserva.getHabitacion().getId(), "OCUPADA");
                System.out.println("Habitación " + savedReserva.getHabitacion().getNumero() + " marcada como OCUPADA por reserva " + savedReserva.getEstadoReserva() + ".");
            } else if ("PENDIENTE".equals(savedReserva.getEstadoReserva())) {
                System.out.println("Reserva " + savedReserva.getId() + " para habitación " + savedReserva.getHabitacion().getNumero() + " creada como PENDIENTE. La habitación permanece DISPONIBLE.");
            } else if ("CANCELADA".equals(savedReserva.getEstadoReserva()) || "FINALIZADA".equals(savedReserva.getEstadoReserva())) {
                habitacionService.actualizarEstadoHabitacion(savedReserva.getHabitacion().getId(), "DISPONIBLE");
                System.out.println("Habitación " + savedReserva.getHabitacion().getNumero() + " marcada como DISPONIBLE por reserva CANCELADA/FINALIZADA.");
            }
        } else {
            System.err.println("Advertencia: No se pudo actualizar el estado de la habitación porque su ID es nulo.");
        }
        return savedReserva;
    }

    public Optional<Reserva> buscarReservaPorId(Long id) {
        return reservaRepository.findById(id);
    }

    public List<Reserva> obtenerTodasLasReservas() {
        return reservaRepository.findAll();
    }

    public Integer calcularDiasEstadia(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null || fechaFin.isBefore(fechaInicio)) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(fechaInicio, fechaFin);
    }

    public Double calcularTotalPagar(Double precioPorNoche, Integer diasEstadia) {
        if (precioPorNoche == null || diasEstadia == null || diasEstadia < 0) {
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

    @Transactional
    public boolean cancelarReserva(Long reservaId) {
        Optional<Reserva> reservaOptional = reservaRepository.findById(reservaId);

        if (reservaOptional.isPresent()) {
            Reserva reserva = reservaOptional.get();

            if ("ACTIVA".equals(reserva.getEstadoReserva()) || "PENDIENTE".equals(reserva.getEstadoReserva())) {
                reserva.setEstadoReserva("CANCELADA");
                reservaRepository.save(reserva);

                Habitacion habitacion = reserva.getHabitacion();
                if (habitacion != null && habitacion.getId() != null) {
                    habitacionService.actualizarEstadoHabitacion(habitacion.getId(), "DISPONIBLE");
                    System.out.println("Habitación " + habitacion.getNumero() + " liberada por cancelación de reserva " + reservaId);
                } else {
                    System.err.println("Advertencia: Habitación asociada a la reserva " + reservaId + " es nula o sin ID.");
                }
                System.out.println("Reserva " + reservaId + " cancelada exitosamente.");
                return true;
            } else {
                System.out.println("La reserva " + reservaId + " no está en estado que permita cancelación (ACTIVA/PENDIENTE), es: " + reserva.getEstadoReserva());
                return false;
            }
        }
        System.out.println("Error: Reserva " + reservaId + " no encontrada para cancelar.");
        return false;
    }

    public long contarTotalReservas() {
        return reservaRepository.count();
    }

    public long contarCheckInsHoy() {
        LocalDate hoy = LocalDate.now();
        return reservaRepository.countByFechaInicioAndEstadoReserva(hoy, "ACTIVA");
    }

    public long contarCheckOutsHoy() {
        LocalDate hoy = LocalDate.now();
        return reservaRepository.countByFechaFinAndEstadoReserva(hoy, "ACTIVA");
    }

    public long contarReservasPorEstado(String estado) {
        return reservaRepository.countByEstadoReserva(estado);
    }

    public Double calcularIngresosTotales() {
        List<Reserva> reservasFinalizadas = reservaRepository.findByEstadoReserva("FINALIZADA");
        return reservasFinalizadas.stream()
                .mapToDouble(Reserva::getTotalPagar)
                .sum();
    }
}