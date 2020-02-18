package com.amdocs.crmdashboardinnovationapp.webcontroller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
	
	@Value("${crm.app.dev}")
	protected String devUrl;
	@Value("${crm.app.prod}")
	protected String productionUrl;
	
	@Override
	public void addCorsMappings(CorsRegistry registry){
		registry.addMapping("/**").allowedOrigins(devUrl, productionUrl);
	}
}
