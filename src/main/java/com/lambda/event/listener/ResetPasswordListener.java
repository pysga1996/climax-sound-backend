package com.lambda.event.listener;

import com.lambda.event.OnResetPasswordEvent;
import com.lambda.model.entities.User;
import com.lambda.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

@Component
public class ResetPasswordListener extends CustomListener implements ApplicationListener<OnResetPasswordEvent> {
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
    public void onApplicationEvent(@NonNull OnResetPasswordEvent event) {
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
