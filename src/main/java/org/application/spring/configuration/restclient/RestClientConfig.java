package org.application.spring.configuration.restclient;

import org.application.spring.configuration.ssl.SslContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration(proxyBeanMethods = false)
public class RestClientConfig {

    @Bean("secureRestClient")
    public RestClient secureRestClient() {
        return SslContextBuilder.createSecureRestClient(
                "PKCS12",
                "classpath:p12/client.p12",
                "client123",
                "classpath:p12/client-truststore.p12",
                "trust123"
        );
    }
}

