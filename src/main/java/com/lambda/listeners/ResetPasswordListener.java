package com.lambda.listeners;

import com.lambda.events.OnResetPasswordEvent;
import com.lambda.models.entities.User;
import com.lambda.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

@Component
public class ResetPasswordListener extends CustomListener implements ApplicationListener<OnResetPasswordEvent> {
    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(OnResetPasswordEvent event) {
        this.confirmResetPassword(event);
    }

    private void confirmResetPassword(OnResetPasswordEvent event) {
        Locale locale;
        try {
            locale = event.getLocale();
        } catch (Exception e) {
            locale = Locale.US;
        }
        String emailSubject = messageSource.getMessage("reset-password.email.title",new Object[] {}, locale);
        String emailText = messageSource.getMessage("reset-password.email.text",new Object[] {}, locale);
        String param = "/api/reset-password?token=";
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createPasswordResetToken(user, token);
        super.sendMail(emailSubject, emailText, param, token, user, event);
    }
}
