package org.application.spring.ddd.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.lang3.CharEncoding;
import org.application.spring.configuration.properties.Properties;
import org.application.spring.ddd.model.entity.User;
import org.springframework.context.annotation.DependsOn;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.File;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
@DependsOn({"properties"})
public class MailService {

    private final String baseUrl = Properties.getEmailBaseUrl();

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    public MailService(JavaMailSender javaMailSender, SpringTemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    public void sendSimpleMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(Properties.getEmailUsername());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }

    public String sendMailFromTemplate(User user, String templateName, String subject) {

        Locale local = Locale.ENGLISH;
        Context context = new Context(local);
        context.setVariable("user", user);
        context.setVariable("baseUrl", baseUrl);
        String content = templateEngine.process(templateName, context);
        return sendMail(user.getUsername(), subject, content, false, true);
    }

    public String sendMail(String to, String subject, String content, boolean isMultiPart, boolean isHTML) {

        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, CharEncoding.UTF_8);

            message.setTo(to);
            //message.setFrom(from);
            message.setSubject(subject);
            message.setText(content, isHTML);

            String path = MailService.class.getResource("").getPath();
            path = MessageFormat.format("{0}/static/images/favicon.ico", path.substring(0, path.indexOf("/classes") + 8));

            File file = new File(path);

            message.addAttachment(file.getName(), file);

            this.javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return "ok";
    }

    @Async
    public Future<String> sendActivationMail(User user) {
        String result = sendMailFromTemplate(user, "email/activation", "User Activation");
        return CompletableFuture.completedFuture(result);
    }

}
