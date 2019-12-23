package com.lambda.events;

import com.lambda.models.entities.User;
import lombok.*;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class OnRegistrationCompleteEvent extends ApplicationEvent implements CustomEvent {
    private String appUrl;
    private Locale locale;
    private User user;

    public OnRegistrationCompleteEvent(User user, Locale locale, String appUrl) {
        super(user);
        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
    }
}
