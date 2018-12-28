package com.registrar.demo;

import com.registrar.demo.annotation.EnableAutoRequestProxy;
import com.registrar.demo.registrar.AutoRequestProxyRegistrar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
//@EnableAutoRequestProxy
@Import(AutoRequestProxyRegistrar.class)
public class RegistrarBeanImportBeanDefinitionRegistrarApplication {

	public static void main(String[] args) {
		SpringApplication.run(RegistrarBeanImportBeanDefinitionRegistrarApplication.class, args);
	}
}
