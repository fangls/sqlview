package com.fang.sqlview;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class SqlViewApplication {

	public static void main(String[] args) {
		SpringApplication.run(SqlViewApplication.class, args);
	}

    @Bean
    public WebMvcConfigurer corsConfigurer() {
      return new WebMvcConfigurer() {
          @Override
          public void addCorsMappings(CorsRegistry registry) {
              registry.addMapping("/**");
          }
      };
    }

    @Bean
    public VelocityEngine initVelocityEngine(){
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        ve.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        ve.setProperty(Velocity.FILE_RESOURCE_LOADER_CACHE, true);
        ve.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        return ve;
    }

}

