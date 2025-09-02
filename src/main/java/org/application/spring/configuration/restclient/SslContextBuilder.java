package org.application.spring.configuration.restclient;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;

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
    ) throws Exception {

        keyStorePath = keyStorePath.replace("classpath:", classpath + "/");
        trustStorePath = trustStorePath.replace("classpath:", classpath + "/");

        // Load client keystore
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
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
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return sslContext;
    }
}

