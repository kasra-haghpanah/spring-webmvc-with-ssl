package org.application.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

// https://localhost:8443/spring/swagger-ui/index.html
@SpringBootApplication
@ImportResource({"classpath:bean-config.xml"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
