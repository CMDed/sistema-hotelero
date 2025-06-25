package com.hotel.gestion.sistema_hotelero;

import com.hotel.gestion.sistema_hotelero.service.HabitacionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SistemaHoteleroApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaHoteleroApplication.class, args);
	}

	@Bean
	public CommandLineRunner initDatabase(HabitacionService habitacionService) {
		return args -> {
			habitacionService.inicializarHabitacionesSiNoExisten();
		};
	}
}