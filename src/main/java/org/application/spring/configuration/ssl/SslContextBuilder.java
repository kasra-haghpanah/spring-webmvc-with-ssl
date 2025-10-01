package org.application.spring.configuration.ssl;

import org.application.spring.configuration.restclient.RestClientConfig;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.net.http.HttpClient;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.time.Duration;

public class SslContextBuilder {

    static final String classpath;

    static {
        String classPath = RestClientConfig.class.getResource("").getPath();
        classPath = classPath.substring(0, classPath.indexOf("/classes") + 8);
        classpath = classPath;
    }

    public static SSLContext buildSslContext(
            String keyStoreType,
            String keyStorePath,
            String keyStorePassword,
            String trustStorePath,
            String trustStorePassword
    ) {

        keyStorePath = keyStorePath.replace("classpath:", classpath + "/");
        trustStorePath = trustStorePath.replace("classpath:", classpath + "/");

        SSLContext sslContext = null;
        // Load client keystore
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(keyStoreType);
            try (FileInputStream keyStoreStream = new FileInputStream(keyStorePath)) {
                keyStore.load(keyStoreStream, keyStorePassword.toCharArray());
            }

            // Load truststore
            KeyStore trustStore = KeyStore.getInstance(keyStoreType);
            try (FileInputStream trustStoreStream = new FileInputStream(trustStorePath)) {
                trustStore.load(trustStoreStream, trustStorePassword.toCharArray());
            }

            // Init KeyManager
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, keyStorePassword.toCharArray());

            // Init TrustManager
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            // Build SSLContext
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            return sslContext;
        }

    }

    public static RestClient createSecureRestClient(SSLContext sslContext) {
        HttpClient httpClient = HttpClient.newBuilder()
                .sslContext(sslContext)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        return RestClient.builder()
                .requestFactory(new JdkClientHttpRequestFactory(httpClient))
                .build();
    }

    public static RestClient createSecureRestClient(
            String keyStoreType,
            String keyStorePath,
            String keyStorePassword,
            String trustStorePath,
            String trustStorePassword
    ) {
        SSLContext sslContext = buildSslContext(
                "PKCS12",
                "classpath:p12/client.p12",
                "client123",
                "classpath:p12/client-truststore.p12",
                "trust123"
        );
        return createSecureRestClient(sslContext);
    }
}

