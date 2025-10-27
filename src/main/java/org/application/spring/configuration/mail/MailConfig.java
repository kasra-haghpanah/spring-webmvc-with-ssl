package org.application.spring.configuration.mail;

import org.application.spring.configuration.properties.Properties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;



@Configuration(proxyBeanMethods = false)
public class MailConfig {

    @Bean
    public SpringTemplateEngine springTemplateEngine(
            @Qualifier("templateResolver") ClassLoaderTemplateResolver templateResolver,
            MessageSource messageSource
    ) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver);
        engine.setMessageSource(messageSource);
        return engine;
    }

    @Bean("templateResolver")
    public ClassLoaderTemplateResolver templateResolver() {
        var resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/email/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        return resolver;
    }

    @Bean
    public MessageSource messageSource() {
        var source = new ResourceBundleMessageSource();
        source.setBasename("messages");
        source.setDefaultEncoding("UTF-8");
        return source;
    }

    @Bean
    public JavaMailSender javaMailSender() {

        // https://www.google.com/settings/security/lesssecureapps
        // https://www.baeldung.com/spring-email
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(Properties.getEmailUsername());
        mailSender.setPassword(Properties.getEmailPassword().replaceAll("\s", ""));
        mailSender.setDefaultEncoding("UTF-8");

        java.util.Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.connectiontimeout", 5000);
        props.put("mail.smtp.timeout", 5000);
        props.put("mail.smtp.writetimeout", 5000);
        // تنظیمات اضافی برای سازگاری بهتر
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        // برای دیباگ مفید است
        props.put("mail.debug", "true");

        return mailSender;
    }


}
