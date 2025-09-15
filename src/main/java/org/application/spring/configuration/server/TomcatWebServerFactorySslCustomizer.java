package org.application.spring.configuration.server;


import org.application.spring.configuration.properties.Properties;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.io.IOException;
import java.net.ServerSocket;


@Configuration
@DependsOn("properties")
public class TomcatWebServerFactorySslCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Override
    public void customize(TomcatServletWebServerFactory serverFactory) {

        serverFactory.addConnectorCustomizers(connector -> {
            connector.setMaxPostSize(600 * 1024 * 1024); // 600MB  => for maximum upload size
            //connector.setMaxSavePostSize(-1); // disable buffering limit
            connector.setProperty("maxSwallowSize", String.valueOf(600 * 1024 * 1024)); // برای جلوگیری از drop شدن
        });

        Ssl ssl = new Ssl();
        ssl.setEnabled(true);
        ssl.setKeyStore("classpath:p12/server.p12");
        ssl.setKeyAlias("serverAlias");
        ssl.setKeyStoreType("PKCS12"); // "JKS"
        ssl.setKeyPassword("server123");
        ssl.setKeyStorePassword("server123");
        serverFactory.setSsl(ssl);
        serverFactory.setPort(getPort()); // یا از Properties بخوانید

    }

    public static int getPort() {
        // دریافت پورت از تنظیمات یا properties
        int preferredPort = Properties.getServerPort();

        // اگر پورت 0 باشد، Spring Boot به صورت خودکار یک پورت آزاد انتخاب می‌کند
        // یا می‌توانید از SocketUtils برای پیدا کردن پورت آزاد استفاده کنید
        int actualPort = preferredPort;

        if (preferredPort == 0) {
            // پیدا کردن پورت آزاد به صورت تصادفی
            actualPort = findAvailableTcpPort(8080, 8999);
        }

        return actualPort;
    }


    public static int findAvailableTcpPort(int minPort, int maxPort) {
        for (int port = minPort; port <= maxPort; port++) {
            try (ServerSocket socket = new ServerSocket(port)) {
                socket.setReuseAddress(true);
                return port;
            } catch (IOException ignored) {
                // پورت در دسترس نیست، برو بعدی
            }
        }
        throw new IllegalStateException("No available port in range " + minPort + " - " + maxPort);
    }


}
