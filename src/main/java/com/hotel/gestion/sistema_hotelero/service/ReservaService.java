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
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

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
    public Reserva crearOActualizarReserva(Reserva reserva) {
        if (reserva.getFechaInicio() == null || reserva.getFechaFin() == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin de la reserva no pueden ser nulas.");
        }
        if (reserva.getFechaFin().isBefore(reserva.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de fin de la reserva no puede ser anterior a la fecha de inicio.");
        }

        Integer diasEstadiaCalculado = calcularDiasEstadia(reserva.getFechaInicio(), reserva.getFechaFin());
        reserva.setDiasEstadia(diasEstadiaCalculado);

        if (reserva.getHabitacion() == null || reserva.getHabitacion().getId() == null) {
            throw new IllegalArgumentException("La reserva debe estar asociada a una habitación válida con un ID.");
        }
        Optional<Habitacion> habitacionOptional = habitacionService.buscarHabitacionPorId(reserva.getHabitacion().getId());
        if (habitacionOptional.isEmpty()) {
            throw new IllegalArgumentException("Habitación con ID " + reserva.getHabitacion().getId() + " no encontrada.");
        }
        Habitacion habitacionAsociada = habitacionOptional.get();
        reserva.setHabitacion(habitacionAsociada);

        Double precioPorNoche = habitacionAsociada.getPrecioPorNoche();

        if (precioPorNoche == null || precioPorNoche <= 0) {
            throw new IllegalStateException("El precio por noche de la habitación " + habitacionAsociada.getNumero() + " no está definido o es cero.");
        }

        Double totalAPagarCalculado = calcularTotalPagar(precioPorNoche, diasEstadiaCalculado);
        reserva.setTotalPagar(totalAPagarCalculado);

        Reserva savedReserva = reservaRepository.save(reserva);

        if (savedReserva.getHabitacion() != null && savedReserva.getHabitacion().getId() != null) {
            if ("ACTIVA".equals(savedReserva.getEstadoReserva())) {
                habitacionService.actualizarEstadoHabitacion(savedReserva.getHabitacion().getId(), "OCUPADA");
                System.out.println("Habitación " + savedReserva.getHabitacion().getNumero() + " marcada como OCUPADA por reserva " + savedReserva.getEstadoReserva() + ".");
            } else if ("PENDIENTE".equals(savedReserva.getEstadoReserva())) {
                System.out.println("Reserva " + savedReserva.getId() + " para habitación " + savedReserva.getHabitacion().getNumero() + " creada como PENDIENTE. La habitación permanece DISPONIBLE.");
            }
        } else {
            System.err.println("Advertencia: No se pudo actualizar el estado de la habitación porque su ID es nulo.");
        }
        return savedReserva;
    }

    public Integer calcularDiasEstadia(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            return 0;
        }
        if (fechaFin.isBefore(fechaInicio)) {
            return 0;
        }

        long daysBetween = ChronoUnit.DAYS.between(fechaInicio, fechaFin);

        if (daysBetween == 0 && fechaInicio.equals(fechaFin)) {
            return 1;
        }

        return (int) daysBetween;
    }

    public Double calcularTotalPagar(Double precioPorNoche, Integer diasEstadia) {
        if (precioPorNoche == null || diasEstadia == null || diasEstadia < 0) {
            return 0.0;
        }
        return precioPorNoche * diasEstadia;
    }

    @Transactional
    public boolean finalizarReserva(Long reservaId) {
        Optional<Reserva> reservaOptional = reservaRepository.findById(reservaId);

        if (reservaOptional.isPresent()) {
            Reserva reserva = reservaOptional.get();

            if ("ACTIVA".equals(reserva.getEstadoReserva())) {
                reserva.setEstadoReserva("FINALIZADA");
                reserva.setFechaSalidaReal(LocalDate.now());
                reservaRepository.save(reserva);

                Habitacion habitacion = reserva.getHabitacion();
                if (habitacion != null && habitacion.getId() != null) {
                    habitacionService.actualizarEstadoHabitacion(habitacion.getId(), "DISPONIBLE");
                    System.out.println("Habitación " + habitacion.getNumero() + " liberada por finalización de reserva " + reservaId);
                } else {
                    System.err.println("Advertencia: Habitación asociada a la reserva " + reservaId + " es nula o sin ID.");
                }
                System.out.println("Reserva " + reservaId + " finalizada exitosamente. Fecha de salida real: " + LocalDate.now());
                return true;
            } else {
                System.out.println("La reserva " + reservaId + " no está en estado ACTIVA para ser finalizada. Estado actual: " + reserva.getEstadoReserva());
                return false;
            }
        }
        System.out.println("Error: Reserva " + reservaId + " no encontrada para finalizar.");
        return false;
    }

    public Optional<Reserva> buscarReservaPorId(Long id) {
        return reservaRepository.findById(id);
    }

    public List<Reserva> obtenerTodasLasReservas() {
        return reservaRepository.findAll();
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
        return reservaRepository.countByFechaInicioAndEstadoReservaIn(hoy, Arrays.asList("ACTIVA", "FINALIZADA"));
    }

    public long contarCheckOutsHoy() {
        LocalDate hoy = LocalDate.now();
        return reservaRepository.countByFechaSalidaReal(hoy);
    }

    public long contarReservasPorEstado(String estado) {
        return reservaRepository.countByEstadoReserva(estado);
    }

    public Double calcularIngresosTotales() {
        return reservaRepository.sumTotalPagarForPendingActiveAndFinalizedReservas();
    }

    public List<Map<String, Object>> getIngresosPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin) {

        List<Reserva> reservasFinalizadas = reservaRepository.findByEstadoReservaAndFechaSalidaRealBetween("FINALIZADA", fechaInicio, fechaFin);

        Map<LocalDate, Double> ingresosPorDia = reservasFinalizadas.stream()
                .filter(r -> r.getFechaSalidaReal() != null)
                .collect(Collectors.groupingBy(
                        Reserva::getFechaSalidaReal,
                        Collectors.summingDouble(Reserva::getTotalPagar)
                ));

        List<Map<String, Object>> result = new ArrayList<>();
        for (LocalDate date = fechaInicio; !date.isAfter(fechaFin); date = date.plusDays(1)) {
            Double ingresos = ingresosPorDia.getOrDefault(date, 0.0);
            result.add(Map.of("fecha", date.format(DateTimeFormatter.ISO_DATE), "ingresos", ingresos));
        }

        result.sort(Comparator.comparing(item -> LocalDate.parse((String) item.get("fecha"))));
        return result;
    }

    public List<Map<String, Object>> getOcupacionPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin, long totalHabitaciones) {
        if (totalHabitaciones == 0) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (LocalDate date = fechaInicio; !date.isAfter(fechaFin); date = date.plusDays(1)) {
            long habitacionesOcupadas = reservaRepository.countActiveReservationsOnDate(date);

            long habitacionesDisponibles = totalHabitaciones - habitacionesOcupadas;
            double ocupacionPorcentaje = (double) habitacionesOcupadas / totalHabitaciones * 100;

            result.add(Map.of(
                    "fecha", date.format(DateTimeFormatter.ISO_DATE),
                    "habitacionesOcupadas", habitacionesOcupadas,
                    "habitacionesDisponibles", habitacionesDisponibles,
                    "ocupacionPorcentaje", ocupacionPorcentaje
            ));
        }
        return result;
    }

    public List<Map<String, Object>> getMovimientoPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (LocalDate date = fechaInicio; !date.isAfter(fechaFin); date = date.plusDays(1)) {
            long checkIns = reservaRepository.countByFechaInicioAndEstadoReservaIn(date, Arrays.asList("ACTIVA", "FINALIZADA"));
            long checkOuts = reservaRepository.countByFechaSalidaReal(date);

            result.add(Map.of(
                    "fecha", date.format(DateTimeFormatter.ISO_DATE),
                    "checkIns", checkIns,
                    "checkOuts", checkOuts
            ));
        }
        return result;
    }
}