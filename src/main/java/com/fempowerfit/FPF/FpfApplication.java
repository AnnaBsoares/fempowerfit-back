package com.fempowerfit.FPF;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FpfApplication {

	public static void main(String[] args) {
		SpringApplication.run(FpfApplication.class, args);
	}

}
