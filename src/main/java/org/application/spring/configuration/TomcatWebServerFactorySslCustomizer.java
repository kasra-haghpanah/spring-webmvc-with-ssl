package org.application.spring.configuration;


import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

//https://www.baeldung.com/spring-boot-reactor-netty
@Configuration
@DependsOn("properties")
public class TomcatWebServerFactorySslCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Override
    public void customize(TomcatServletWebServerFactory serverFactory) {

        // keytool -genkeypair -alias alias -keyalg RSA -keysize 2048 -storetype JKS -keystore sample.jks -validity 3650

        // keytool -list -v -keystore C:\Users\98911\Desktop\Technical-inistitue-of-Tehran\project\spring\target\classes\jks\sample.jks
        Ssl ssl = new Ssl();
        ssl.setEnabled(true);
        ssl.setKeyStore("classpath:jks/sample.jks");
        ssl.setKeyAlias("alias");
        ssl.setKeyStoreType("JKS");
        ssl.setKeyPassword("kasra123");
        ssl.setKeyStorePassword("kasra123");
        serverFactory.setSsl(ssl);
        serverFactory.setPort(Properties.getServerPort()); // یا از Properties بخوانید

        //serverFactory.setPort(Properties.getServerPort());
/*

        InetAddress address = null;
        try {
            address = InetAddress.getByName("127.0.0.1");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        serverFactory.setAddress(address);
*/
        //final String applicationPath = MessageFormat.format("/{0}",environment.getProperty("spring.application.name"));
        //((ConfigurableServletWebServerFactory) serverFactory).setContextPath(applicationPath);


    }


}
