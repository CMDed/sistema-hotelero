package com.hotel.gestion.sistema_hotelero.repository;

import com.hotel.gestion.sistema_hotelero.model.Reserva;
import com.hotel.gestion.sistema_hotelero.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByCliente(Cliente cliente);
    List<Reserva> findByClienteAndEstadoReserva(Cliente cliente, String estadoReserva);

    long countByFechaInicioAndEstadoReserva(LocalDate fechaInicio, String estadoReserva);
    long countByFechaFinAndEstadoReserva(LocalDate fechaFin, String estadoReserva);

    long countByFechaInicioAndEstadoReservaIn(LocalDate fechaInicio, List<String> estadoReserva);
    long countByFechaFinAndEstadoReservaIn(LocalDate fechaFin, List<String> estadoReserva);

    long countByEstadoReserva(String estadoReserva);

    List<Reserva> findByEstadoReserva(String estadoReserva);
}