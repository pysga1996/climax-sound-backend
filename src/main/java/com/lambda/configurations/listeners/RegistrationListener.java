package com.lambda.configurations.listeners;


import com.lambda.configurations.events.OnRegistrationCompleteEvent;
import com.lambda.models.entities.User;
import com.lambda.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    @Autowired
    private UserService service;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment environment;

    @Autowired
    private MessageSource messageSource;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        Locale locale;
        try {
            locale = event.getLocale();
        } catch (Exception e) {
            locale = Locale.US;
        }
        String emailSubject = messageSource.getMessage("registration.email.title",new Object[] {}, locale);
        String emailText = messageSource.getMessage("registration.email.text",new Object[] {}, locale);
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        service.createVerificationToken(user, token);
        String recipientAddress = user.getEmail();
        String confirmationUrl = event.getAppUrl() + "/api/registration-confirm?token=" + token;
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(emailSubject);
        email.setText(emailText + "\n" + environment.getProperty("BACKEND_HOST") + confirmationUrl);
        mailSender.send(email);
    }
}
