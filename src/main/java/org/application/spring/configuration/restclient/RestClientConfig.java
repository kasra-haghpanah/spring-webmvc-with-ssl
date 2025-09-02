package org.application.spring.configuration.restclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.client.RestClient;

@Configuration
@DependsOn({"properties"})
public class RestClientConfig {

    @Bean("secureRestClient")
    public RestClient secureRestClient() throws Exception {
        return SslContextBuilder.createSecureRestClient(
                "PKCS12",
                "classpath:p12/client.p12",
                "client123",
                "classpath:p12/client-truststore.p12",
                "trust123"
        );
    }
}

