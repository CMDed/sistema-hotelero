package com.hotel.gestion.sistema_hotelero.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.core.userdetails.User; // Importa User
import org.springframework.security.core.userdetails.UserDetails; // Importa UserDetails
import org.springframework.security.core.userdetails.UserDetailsService; // Importa UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager; // Importa InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilita CSRF (temporalmente para H2 y desarrollo simple)
            .headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin)) // Habilita frames para H2 console
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll() // Permite acceso a H2 console
                .requestMatchers(new AntPathRequestMatcher("/login")).permitAll() // Permite acceso a la página de login
                .requestMatchers(new AntPathRequestMatcher("/css/**")).permitAll() // Permite acceso a archivos CSS
                .requestMatchers(new AntPathRequestMatcher("/js/**")).permitAll()   // Permite acceso a archivos JS
                .requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll() // Permite acceso a imágenes
                .anyRequest().authenticated() // Cualquier otra solicitud requiere autenticación
            )
            .formLogin(form -> form
                .loginPage("/login") // Especifica la página de login
                .defaultSuccessUrl("/dashboard", true) // URL a la que ir después de un login exitoso
                .permitAll() // Permite a todos acceder al formulario de login
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // URL para logout
                .logoutSuccessUrl("/login?logout") // URL después de logout exitoso
                .permitAll()
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.builder()
            .username("admin") // Usuario de prueba para el administrador
            .password(passwordEncoder.encode("adminpass")) // Contraseña encriptada
            .roles("ADMIN") // Rol
            .build();

        UserDetails recepcionista = User.builder()
            .username("recepcionista") // Usuario de prueba para el recepcionista
            .password(passwordEncoder.encode("recepcionistapass")) // Contraseña encriptada
            .roles("RECEPCIONISTA") // Rol
            .build();

        return new InMemoryUserDetailsManager(admin, recepcionista);
    }

}