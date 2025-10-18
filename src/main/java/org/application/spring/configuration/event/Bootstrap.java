package org.application.spring.configuration.event;

import org.application.spring.configuration.websocket.client.WebSocketClientApp;
import org.application.spring.ddd.model.entity.User;
import org.application.spring.ddd.service.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.annotation.Order;

import java.util.Arrays;

@Configuration
public class Bootstrap {

    @Bean(value = "appRunner")
    @Order(value = 1)
    public ApplicationRunner applicationRunner(UserService userService) {


        String userName = "application@gmail.com";
        String password = "123";
        User application = userService.findByUserName(userName);
        if (application == null) {
            User user = new User();
            user.setUserName(userName);
            user.setPassword(password);
            user.setFirstName("application");
            user.setLastName("spring");
            user.setPhoneNumber("09113394954");
            //user.setActivationCode(UUID.randomUUID().toString());
            user.addAuthorities("ADMIN", "USER");
            user = userService.save(user);
        }

        return (arg) -> {
            // java -Dserver.port=8442 -jar spring.jar arg1 arg2 arg3

            System.out.println("applicationRunner -> appRunner ");
            // دسترسی به تمام آرگومان‌های خام
            System.out.println("Raw arguments: " + arg.getSourceArgs());

            // آرگومان‌های named (با --)
            System.out.println("Option names: " + arg.getOptionNames());
            System.out.println("--name value: " + arg.getOptionValues("name"));

            // آرگومان‌های غیر named (بدون --)
            System.out.println("Non-option args: " + arg.getNonOptionArgs());

            //WebSocketClientApp.login(userName, password);


        };

    }

    @Bean(value = "clrOne")
    @Order(value = 1)
    public CommandLineRunner commandLineRunnerOne() {

        return (arg) -> {
            // java -Dserver.port=8442 -jar spring.jar arg1 arg2 arg3
            System.out.println("commandLineRunner -> one " + Arrays.toString(arg));
        };

    }

    @Bean(value = "clrTwo")
    @Order(value = 2)
    public CommandLineRunner commandLineRunnerTwo() {

        return (arg) -> {
            System.out.println("commandLineRunner -> two" + Arrays.toString(arg));
        };

    }


    @Bean
    ApplicationListener<ContextClosedEvent> closedEvent() {

        return (contextClosedEvent) -> {

            System.out.println("when the application gets shut down this lambda will be called.");
        };

    }

}
