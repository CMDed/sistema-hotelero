package com.hotel.gestion.sistema_hotelero.service;

import com.hotel.gestion.sistema_hotelero.model.Cliente;
import com.hotel.gestion.sistema_hotelero.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente guardarCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Optional<Cliente> buscarClientePorDni(String dni) {
        return clienteRepository.findByDni(dni);
    }

    public boolean existeClientePorDni(String dni) {
        return clienteRepository.findByDni(dni).isPresent();
    }

    public List<Cliente> obtenerTodosLosClientes() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarClientePorId(Long id) {
        return clienteRepository.findById(id);
    }

    public long contarTotalClientes() {
        return clienteRepository.count();
    }
}