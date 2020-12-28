package com.lambda.event;

import com.lambda.model.entities.User;

public interface CustomEvent {
    String getAppUrl();
    User getUser();
}
