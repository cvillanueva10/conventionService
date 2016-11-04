package org.akpsi.conventionapp;

import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
    	SpringApplication app = new SpringApplication(applicationClass, args);
    	ApplicationContext context =
        		new ClassPathXmlApplicationContext("Spring-Module.xml");
    	app.setDefaultProperties(getProperties());
    	app.run();
    }

    private static Properties getProperties(){
    	Properties prop = new Properties();
//    	prop.setProperty("spring.config.location", "file:///C:/properties/convention.properties");
    	System.out.println("System property=" + System.getProperty("spring.config.location"));
    	prop.setProperty("spring.config.location", System.getProperty("spring.config.location"));
    	return prop;
    }
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(applicationClass);
    }

    private static Class<Application> applicationClass = Application.class;
}