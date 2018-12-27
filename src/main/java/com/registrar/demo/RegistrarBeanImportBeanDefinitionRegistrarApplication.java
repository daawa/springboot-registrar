package com.registrar.demo;

import com.registrar.demo.annotation.EnableAutoRequestProxy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoRequestProxy
public class RegistrarBeanImportBeanDefinitionRegistrarApplication {

	public static void main(String[] args) {
		SpringApplication.run(RegistrarBeanImportBeanDefinitionRegistrarApplication.class, args);
	}
}
