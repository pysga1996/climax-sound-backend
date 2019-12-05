package com.lambda.configurations.events;

import com.lambda.models.entities.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Data
@EqualsAndHashCode(callSuper = true)
public class OnResetPasswordEvent extends ApplicationEvent implements CustomEvent {
    private String appUrl;
    private Locale locale;
    private User user;

    public OnResetPasswordEvent(User user, Locale locale, String appUrl) {
        super(user);
        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
    }
}
