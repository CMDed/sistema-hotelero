package com.hotel.gestion.sistema_hotelero;

import com.hotel.gestion.sistema_hotelero.model.Usuario;
import com.hotel.gestion.sistema_hotelero.repository.UsuarioRepository;
import com.hotel.gestion.sistema_hotelero.service.HabitacionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SistemaHoteleroApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaHoteleroApplication.class, args);
	}

	@Bean
	public CommandLineRunner initDatabase(
			HabitacionService habitacionService,
			UsuarioRepository usuarioRepository,
			PasswordEncoder passwordEncoder) {
		return args -> {
			habitacionService.inicializarHabitacionesSiNoExisten();
			System.out.println("Habitaciones inicializadas/verificadas.");

			if (usuarioRepository.findByUsername("admin").isEmpty()) {
				Usuario admin = new Usuario();
				admin.setUsername("admin");
				admin.setPassword(passwordEncoder.encode("adminpass"));
				admin.setRol("ROLE_ADMIN");
				usuarioRepository.save(admin);
				System.out.println("Usuario 'admin' creado con rol ROLE_ADMIN.");
			} else {
				System.out.println("Usuario 'admin' ya existe.");
			}

			if (usuarioRepository.findByUsername("recepcionista").isEmpty()) {
				Usuario recepcionista = new Usuario();
				recepcionista.setUsername("recepcionista");
				recepcionista.setPassword(passwordEncoder.encode("recepcionistapass"));
				recepcionista.setRol("ROLE_RECEPCIONISTA");
				usuarioRepository.save(recepcionista);
				System.out.println("Usuario 'recepcionista' creado con rol ROLE_RECEPCIONISTA.");
			} else {
				System.out.println("Usuario 'recepcionista' ya existe.");
			}
		};
	}
}