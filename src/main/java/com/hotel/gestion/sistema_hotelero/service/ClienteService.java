package com.hotel.gestion.sistema_hotelero.service;

import com.hotel.gestion.sistema_hotelero.model.Cliente;
import com.hotel.gestion.sistema_hotelero.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final AuditoriaService auditoriaService;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository, AuditoriaService auditoriaService) {
        this.clienteRepository = clienteRepository;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public Cliente crearCliente(Cliente cliente) {
        if (clienteRepository.findByDni(cliente.getDni()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un cliente con el DNI '" + cliente.getDni() + "'.");
        }
        if (cliente.getEmail() != null && !cliente.getEmail().isEmpty() && clienteRepository.findByEmail(cliente.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un cliente con el email '" + cliente.getEmail() + "'.");
        }

        Cliente nuevoCliente = clienteRepository.save(cliente);

        auditoriaService.registrarAccion(
                "CREACION_CLIENTE",
                "Nuevo cliente registrado: " + nuevoCliente.getNombres() + " " + nuevoCliente.getApellidos() + " (DNI: " + nuevoCliente.getDni() + ")",
                "Cliente",
                nuevoCliente.getId()
        );

        return nuevoCliente;
    }

    @Transactional
    public Cliente actualizarCliente(Cliente clienteActualizado) {
        return clienteRepository.findById(clienteActualizado.getId())
                .map(clienteExistente -> {
                    if (!clienteExistente.getDni().equals(clienteActualizado.getDni()) && clienteRepository.findByDni(clienteActualizado.getDni()).isPresent()) {
                        throw new IllegalArgumentException("El DNI '" + clienteActualizado.getDni() + "' ya está en uso por otro cliente.");
                    }
                    if (clienteActualizado.getEmail() != null && !clienteActualizado.getEmail().isEmpty() && !clienteActualizado.getEmail().equals(clienteExistente.getEmail()) && clienteRepository.findByEmail(clienteActualizado.getEmail()).isPresent()) {
                        throw new IllegalArgumentException("El Email '" + clienteActualizado.getEmail() + "' ya está en uso por otro cliente.");
                    }

                    clienteExistente.setNombres(clienteActualizado.getNombres());
                    clienteExistente.setApellidos(clienteActualizado.getApellidos());
                    clienteExistente.setDni(clienteActualizado.getDni());
                    clienteExistente.setNacionalidad(clienteActualizado.getNacionalidad());
                    clienteExistente.setEmail(clienteActualizado.getEmail());
                    clienteExistente.setTelefono(clienteActualizado.getTelefono());

                    Cliente clienteGuardado = clienteRepository.save(clienteExistente);

                    auditoriaService.registrarAccion(
                            "ACTUALIZACION_CLIENTE",
                            "Cliente '" + clienteGuardado.getNombres() + " " + clienteGuardado.getApellidos() + "' (ID: " + clienteGuardado.getId() + ") actualizado.",
                            "Cliente",
                            clienteGuardado.getId()
                    );

                    return clienteGuardado;
                })
                .orElseThrow(() -> new IllegalArgumentException("Cliente con ID " + clienteActualizado.getId() + " no encontrado para actualizar."));
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

    @Transactional
    public void eliminarCliente(Long id) {
        Optional<Cliente> clienteOptional = clienteRepository.findById(id);
        if (clienteOptional.isPresent()) {
            Cliente cliente = clienteOptional.get();
            clienteRepository.deleteById(id);

            auditoriaService.registrarAccion(
                    "ELIMINACION_CLIENTE",
                    "Cliente '" + cliente.getNombres() + " " + cliente.getApellidos() + "' (ID: " + cliente.getId() + ") eliminado.",
                    "Cliente",
                    cliente.getId()
            );
        } else {
            throw new IllegalArgumentException("Cliente con ID " + id + " no encontrado para eliminar.");
        }
    }
}