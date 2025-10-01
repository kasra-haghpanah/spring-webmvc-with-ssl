package org.application.spring.configuration.event;

import org.application.spring.configuration.exception.ApplicationException;
import org.application.spring.configuration.properties.Properties;
import org.application.spring.configuration.security.AuthenticationRequest;
import org.application.spring.configuration.security.AuthenticationResponse;
import org.application.spring.ddd.model.entity.User;
import org.application.spring.ddd.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.UUID;

@Configuration
public class Bootstrap {

    @Bean(value = "appRunner")
    @Order(value = 1)
    public ApplicationRunner applicationRunner(
            @Qualifier("secureRestClient") RestClient restClient,
            UserService userService
    ) {


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
            //restClient.get().retrieve().body(AuthenticationResponse.class);
            System.out.println("applicationRunner -> appRunner ");
            // دسترسی به تمام آرگومان‌های خام
            System.out.println("Raw arguments: " + arg.getSourceArgs());

            // آرگومان‌های named (با --)
            System.out.println("Option names: " + arg.getOptionNames());
            System.out.println("--name value: " + arg.getOptionValues("name"));

            // آرگومان‌های غیر named (بدون --)
            System.out.println("Non-option args: " + arg.getNonOptionArgs());


            AuthenticationRequest authenticationRequest = new AuthenticationRequest(userName, password);

            AuthenticationResponse response = restClient.post()
                    .uri("https://localhost:8443/spring/login")
                    .header("Accept-Language", "fa")
                    .body(authenticationRequest)
                    .exchange((clientRequest, clientResponse) -> {

                        if (clientResponse.getStatusCode().isError()) {
                            throw new ApplicationException("url.invalid", HttpStatus.resolve(HttpStatus.BAD_REQUEST.value()), null);
                        }
                        clientResponse.getHeaders()
                                .forEach((key, values) -> {
                                    if (values != null) {
                                        for (String value : values) {
                                            //response.addHeader(key, value);
                                        }
                                    }
                                });
                        return clientResponse.bodyTo(AuthenticationResponse.class);
                    });

            Properties.token = response.token();

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
