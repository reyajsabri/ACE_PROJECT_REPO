package com.sapient;

import java.util.Arrays;

import javax.servlet.ServletException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.context.WebApplicationContext;

import com.sapient.config.AppInitializer;


@SpringBootApplication
@EnableConfigurationProperties
public class Application extends SpringBootServletInitializer {

	 @Override
	    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	        return application.sources(AppInitializer.class, Application.class);
	    }
	 
	public static void main (String[] args) throws ServletException {
//		Class<?>[] arr = {AppInitializer.class, Application.class}; 
//		Application app = new Application();
//		WebApplicationContext  webContext = app.run(new SpringApplication(arr));
//		app.onStartup(webContext.getServletContext());
		SpringApplication.run(Application.class, args);
    }
}
