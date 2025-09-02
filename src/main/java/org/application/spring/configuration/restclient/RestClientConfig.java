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

        // for server
        //java_path/bin/keytool.exe
        // keytool -genkeypair -alias myAlias -keyalg RSA -keysize 2048 -storetype JKS -keystore sample.jks -validity 3650


        /* for client
        # ساخت keystore کلاینت (حاوی کلید خصوصی و گواهی کلاینت)
        keytool -genkeypair -alias clientAlias -keyalg RSA -keystore client.jks -storepass client123 -keypass client123 -dname "CN=Client"

        # استخراج گواهی کلاینت
        client.cer => keytool -export -alias clientAlias -keystore client.jks -file client.cer -storepass client123


        server.cer => keytool -export -alias myAlias -keystore sample.jks -file server.cer -storepass kasra123
        # ساخت truststore کلاینت (برای اعتماد به سرور)
        keytool -import -alias serverAlias -file server.cer -keystore client-truststore.jks -storepass trust123

        * */

        String clientPath = RestClientConfig.class.getResource("").getPath();
        clientPath = MessageFormat.format("{0}/jks/client.jks", clientPath.substring(0, clientPath.indexOf("/classes") + 8));


        String truststorePath = RestClientConfig.class.getResource("").getPath();
        truststorePath = MessageFormat.format("{0}/jks/client.jks", truststorePath.substring(0, truststorePath.indexOf("/classes") + 8));


        SSLContext sslContext = SslContextBuilder.buildSslContext(
                clientPath, "client123",
                truststorePath, "trust123"
        );

        return RestClientFactory.createSecureRestClient(sslContext);

    }
}

