package org.application.spring.configuration;


import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
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

        //java_path/bin/keytool.exe
        // keytool -genkeypair -alias myAlias -keyalg RSA -keysize 2048 -storetype JKS -keystore sample.jks -validity 3650

        // keytool -list -v -keystore C:\Users\98911\Desktop\Technical-inistitue-of-Tehran\project\spring\target\classes\jks\sample.jks
        Ssl ssl = new Ssl();
        ssl.setEnabled(true);
        ssl.setKeyStore("classpath:jks/sample.jks");
        ssl.setKeyAlias("myAlias");
        ssl.setKeyStoreType("JKS");
        ssl.setKeyPassword("kasra123");
        ssl.setKeyStorePassword("kasra123");
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
