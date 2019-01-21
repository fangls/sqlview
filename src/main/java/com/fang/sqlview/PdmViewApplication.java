package com.fang.sqlview;

import cn.hutool.extra.template.Engine;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateUtil;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Properties;

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

    @Bean
    public VelocityEngine initVelocityEngine(){
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        ve.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        ve.setProperty(Velocity.FILE_RESOURCE_LOADER_CACHE, true);
        ve.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        return ve;
    }

    @Bean
    public Engine initEngine(){

        TemplateConfig config = new TemplateConfig("templates", TemplateConfig.ResourceMode.CLASSPATH);

        Engine engine = TemplateUtil.createEngine(config);
        return engine;
    }

}

