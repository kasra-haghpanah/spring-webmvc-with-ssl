package org.application.spring.configuration.restclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import javax.net.ssl.SSLContext;
import java.text.MessageFormat;

@Configuration
public class RestClientConfig {

    @Bean("secureRestClient")
    public RestClient secureRestClient() throws Exception {

        String clientPath = RestClientConfig.class.getResource("").getPath();
        clientPath = MessageFormat.format("{0}/p12/client.p12", clientPath.substring(0, clientPath.indexOf("/classes") + 8));


        String truststorePath = RestClientConfig.class.getResource("").getPath();
        truststorePath = MessageFormat.format("{0}/p12/client-truststore.p12", truststorePath.substring(0, truststorePath.indexOf("/classes") + 8));


        SSLContext sslContext = SslContextBuilder.buildSslContext(
                "PKCS12", // "JKS"
                clientPath, "client123",
                truststorePath, "trust123"
        );

        return RestClientFactory.createSecureRestClient(sslContext);

    }
}

