package com.hotel.gestion.sistema_hotelero.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/login")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/css/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/js/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll()


                        .requestMatchers(new AntPathRequestMatcher("/clientes/registrar")).hasAnyAuthority("ROLE_ADMIN", "ROLE_RECEPCIONISTA")
                        .requestMatchers(new AntPathRequestMatcher("/clientes/guardar")).hasAnyAuthority("ROLE_ADMIN", "ROLE_RECEPCIONISTA")
                        .requestMatchers(new AntPathRequestMatcher("/clientes/buscarPorDni")).hasAnyAuthority("ROLE_ADMIN", "ROLE_RECEPCIONISTA")
                        .requestMatchers(new AntPathRequestMatcher("/clientes/historial")).hasAnyAuthority("ROLE_ADMIN", "ROLE_RECEPCIONISTA")
                        .requestMatchers(new AntPathRequestMatcher("/clientes/historial/buscar")).hasAnyAuthority("ROLE_ADMIN", "ROLE_RECEPCIONISTA")

                        .requestMatchers(new AntPathRequestMatcher("/dashboard")).authenticated()
                        .anyRequest().authenticated()
                )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
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
            .username("admin")
            .password(passwordEncoder.encode("adminpass"))
            .roles("ADMIN")
            .build();

        UserDetails recepcionista = User.builder()
            .username("recepcionista")
            .password(passwordEncoder.encode("recepcionistapass"))
            .roles("RECEPCIONISTA")
            .build();

        return new InMemoryUserDetailsManager(admin, recepcionista);
    }

}