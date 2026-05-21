package com.mypham;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class MyphamApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(MyphamApplication.class, args);
	}
}
