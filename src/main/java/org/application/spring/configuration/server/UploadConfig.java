package org.application.spring.configuration.server;

import jakarta.servlet.MultipartConfigElement;
import org.application.spring.configuration.properties.Properties;
import org.springframework.boot.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.util.unit.DataSize;

@Configuration
@DependsOn({"properties"})
public class UploadConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(Properties.getApplicationUploadFileSizeInMB()));
        factory.setMaxRequestSize(DataSize.ofMegabytes(Properties.getApplicationUploadFileSizeInMB()));
        return factory.createMultipartConfig();
    }

}
