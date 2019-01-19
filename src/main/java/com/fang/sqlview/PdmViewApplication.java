package com.fang.sqlview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class })
@EnableTransactionManagement
public class PdmViewApplication {

	public static void main(String[] args) {
		SpringApplication.run(PdmViewApplication.class, args);
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

}

