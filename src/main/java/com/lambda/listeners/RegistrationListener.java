package com.lambda.listeners;


import com.lambda.events.OnRegistrationCompleteEvent;
import com.lambda.models.entities.User;
import com.lambda.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

@Component
public class RegistrationListener extends CustomListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private MessageSource messageSource;

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

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
        String param = "/api/registration-confirm?token=";
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user, token);
        super.sendMail(emailSubject, emailText, param, token, user, event);
    }
}
