package org.application.spring.configuration.restclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.client.RestClient;

import javax.net.ssl.SSLContext;

@Configuration
@DependsOn({"properties"})
public class RestClientConfig {

    @Bean("secureRestClient")
    public RestClient secureRestClient() throws Exception {
        SSLContext sslContext = SslContextBuilder.buildSslContext(
                "PKCS12",
                "classpath:p12/client.p12",
                "client123",
                "classpath:p12/client-truststore.p12",
                "trust123"
        );
        return RestClientFactory.createSecureRestClient(sslContext);
    }
}

