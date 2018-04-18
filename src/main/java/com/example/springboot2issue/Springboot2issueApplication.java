package com.example.springboot2issue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class Springboot2issueApplication {

	public static void main(String[] args) {
		SpringApplication.run(Springboot2issueApplication.class, args);
	}
}
