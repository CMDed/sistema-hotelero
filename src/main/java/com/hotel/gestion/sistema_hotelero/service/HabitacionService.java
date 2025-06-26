package com.hotel.gestion.sistema_hotelero.service;

import com.hotel.gestion.sistema_hotelero.model.Habitacion;
import com.hotel.gestion.sistema_hotelero.repository.HabitacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HabitacionService {
    private final HabitacionRepository habitacionRepository;

    @Autowired
    public HabitacionService(HabitacionRepository habitacionRepository) {
        this.habitacionRepository = habitacionRepository;
    }

    public List<Habitacion> obtenerTodasLasHabitaciones() {
        return habitacionRepository.findAll();
    }

    public Optional<Habitacion> buscarHabitacionPorId(Long id) {
        return habitacionRepository.findById(id);
    }

    public Optional<Habitacion> buscarHabitacionPorNumero(String numero) {
        return habitacionRepository.findByNumero(numero);
    }

    public List<Habitacion> obtenerHabitacionesDisponibles() {
        return habitacionRepository.findByEstado("DISPONIBLE");
    }

    public long contarTotalHabitaciones() {
        return habitacionRepository.count();
    }

    public List<Habitacion> obtenerHabitacionesOcupadas() {
        return habitacionRepository.findByEstado("OCUPADA");
    }

    public List<Habitacion> obtenerHabitacionesEnMantenimiento() {
        return habitacionRepository.findByEstado("MANTENIMIENTO");
    }

    public Habitacion guardarHabitacion(Habitacion habitacion) {
        return habitacionRepository.save(habitacion);
    }

    public void actualizarEstadoHabitacion(Long id, String nuevoEstado) {
        habitacionRepository.findById(id).ifPresent(habitacion -> {
            habitacion.setEstado(nuevoEstado);
            habitacionRepository.save(habitacion);
        });
    }

    public void inicializarHabitacionesSiNoExisten() {
        if (habitacionRepository.count() == 0) {
            habitacionRepository.save(new Habitacion("101", "Simple", 50.0, "DISPONIBLE"));
            habitacionRepository.save(new Habitacion("102", "Doble", 80.0, "DISPONIBLE"));
            habitacionRepository.save(new Habitacion("103", "Suite", 150.0, "DISPONIBLE"));
            habitacionRepository.save(new Habitacion("201", "Simple", 55.0, "DISPONIBLE"));
            habitacionRepository.save(new Habitacion("202", "Doble", 85.0, "OCUPADA"));
            habitacionRepository.save(new Habitacion("203", "Suite", 160.0, "MANTENIMIENTO"));
            System.out.println("Habitaciones inicializadas en la base de datos.");
        }
    }

    public void eliminarHabitacion(Long id) {
        habitacionRepository.deleteById(id);
    }
}