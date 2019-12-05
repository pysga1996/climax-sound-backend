package com.lambda.configurations.listeners;

import com.lambda.configurations.events.CustomEvent;
import com.lambda.models.entities.User;
import com.lambda.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Scope("singleton")
public abstract class CustomListener {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment environment;

    public void sendMail(String emailSubject, String emailText, String param, String token, User user, CustomEvent event) {
        String recipientAddress = user.getEmail();
        String confirmationUrl = event.getAppUrl() + param + token;
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(emailSubject);
        email.setText(emailText + "\n" + environment.getProperty("BACKEND_HOST") + confirmationUrl + "&id=" + user.getId());
        mailSender.send(email);
    }
}
