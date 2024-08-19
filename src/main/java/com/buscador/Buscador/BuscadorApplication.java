package com.buscador.Buscador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.buscador.Buscador")
public class BuscadorApplication {
	public static void main(String[] args) {
		SpringApplication.run(BuscadorApplication.class, args);
	}

}
