package com.hotel.gestion.sistema_hotelero.service;

import com.hotel.gestion.sistema_hotelero.model.Empleado;
import com.hotel.gestion.sistema_hotelero.model.Usuario;
import com.hotel.gestion.sistema_hotelero.repository.EmpleadoRepository;
import com.hotel.gestion.sistema_hotelero.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public EmpleadoService(EmpleadoRepository empleadoRepository, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.empleadoRepository = empleadoRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Empleado registrarRecepcionista(Empleado empleado) {
        if (empleadoRepository.findByDni(empleado.getDni()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un empleado con el DNI proporcionado.");
        }
        if (usuarioRepository.findByUsername(empleado.getUsuario().getUsername()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con el nombre de usuario proporcionado.");
        }

        String rawPassword = empleado.getUsuario().getPassword();
        System.out.println("DEBUG: Contraseña en crudo (antes de codificar): " + rawPassword);
        String encodedPassword = passwordEncoder.encode(rawPassword);
        System.out.println("DEBUG: Contraseña codificada (hash): " + encodedPassword);

        empleado.getUsuario().setPassword(encodedPassword);
        empleado.getUsuario().setRol("ROLE_RECEPCIONISTA");

        return empleadoRepository.save(empleado);
    }

    public List<Empleado> obtenerTodosLosEmpleados() {
        return empleadoRepository.findAll();
    }

    public Optional<Empleado> buscarEmpleadoPorId(Long id) {
        return empleadoRepository.findById(id);
    }

    @Transactional
    public Empleado actualizarEmpleado(Empleado empleadoActualizado) {
        return empleadoRepository.findById(empleadoActualizado.getId())
                .map(empleadoExistente -> {
                    empleadoExistente.setNombres(empleadoActualizado.getNombres());
                    empleadoExistente.setApellidos(empleadoActualizado.getApellidos());
                    empleadoExistente.setDni(empleadoActualizado.getDni());
                    empleadoExistente.setEmail(empleadoActualizado.getEmail());
                    empleadoExistente.setTelefono(empleadoActualizado.getTelefono());

                    Usuario usuarioExistente = empleadoExistente.getUsuario();
                    Usuario usuarioActualizadoForm = empleadoActualizado.getUsuario();

                    if (!usuarioExistente.getUsername().equals(usuarioActualizadoForm.getUsername())) {
                        Optional<Usuario> existingUserWithNewUsername = usuarioRepository.findByUsername(usuarioActualizadoForm.getUsername());
                        if (existingUserWithNewUsername.isPresent() && !existingUserWithNewUsername.get().getId().equals(usuarioExistente.getId())) {
                            throw new IllegalArgumentException("El nombre de usuario '" + usuarioActualizadoForm.getUsername() + "' ya está en uso por otro empleado.");
                        }
                        usuarioExistente.setUsername(usuarioActualizadoForm.getUsername());
                    }

                    if (usuarioActualizadoForm.getPassword() != null && !usuarioActualizadoForm.getPassword().isEmpty()) {
                        String encodedPassword = passwordEncoder.encode(usuarioActualizadoForm.getPassword());
                        usuarioExistente.setPassword(encodedPassword);
                    }

                    return empleadoRepository.save(empleadoExistente);
                })
                .orElseThrow(() -> new IllegalArgumentException("Empleado con ID " + empleadoActualizado.getId() + " no encontrado para actualizar."));
    }

    @Transactional
    public boolean eliminarEmpleado(Long id) {
        return empleadoRepository.findById(id)
                .map(empleado -> {
                    empleadoRepository.delete(empleado);
                    return true;
                }).orElse(false);
    }
}