package com.hotel.gestion.sistema_hotelero.config;

import com.hotel.gestion.sistema_hotelero.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsServiceImpl;

    public SecurityConfig(UserDetailsServiceImpl userDetailsServiceImpl) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/h2-console/**").permitAll()

                                .requestMatchers("/empleados/registrar").hasRole("ADMIN")
                                .requestMatchers("/empleados/**").hasRole("ADMIN")
                                .requestMatchers("/admin/**").hasRole("ADMIN")

                                .requestMatchers("/dashboard").authenticated()
                                .requestMatchers("/clientes/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_RECEPCIONISTA")
                                .requestMatchers("/reservas/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_RECEPCIONISTA")
                                .requestMatchers("/habitaciones").hasAnyAuthority("ROLE_ADMIN", "ROLE_RECEPCIONISTA")
                                .anyRequest().authenticated()
                )
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/login")
                                .defaultSuccessUrl("/dashboard", true)
                                .permitAll()
                )
                .logout(logout ->
                        logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/login?logout")
                                .permitAll()
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsServiceImpl);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /*
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
    */
}