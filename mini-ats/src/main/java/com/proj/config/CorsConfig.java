package com.proj.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Value;


public class CorsConfig {
		
	 @Value("${cors.allowed-origins}")
	    private String[] allowedOrigins;

	    @Bean
	    public WebMvcConfigurer corsConfigurer() {
	        return new WebMvcConfigurer() {
	            @Override
	            public void addCorsMappings(CorsRegistry registry) {
	                registry.addMapping("/api/**")
	                        .allowedOrigins(allowedOrigins)
	                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
	                        .allowedHeaders("*");
	            }
	        };
	    }
	
}
