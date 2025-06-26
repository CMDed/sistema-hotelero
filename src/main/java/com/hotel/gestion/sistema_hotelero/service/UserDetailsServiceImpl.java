package com.hotel.gestion.sistema_hotelero.service;

import com.hotel.gestion.sistema_hotelero.model.Usuario;
import com.hotel.gestion.sistema_hotelero.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("DEBUG: Intentando cargar usuario con username: " + username);

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("DEBUG: Usuario '" + username + "' NO encontrado.");
                    return new UsernameNotFoundException("Usuario no encontrado con username: " + username);
                });

        System.out.println("DEBUG: Usuario '" + username + "' ENCONTRADO. Rol: " + usuario.getRol());
        return usuario;
    }
}