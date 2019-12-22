package com.lambda.helpers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.lambda.models.entities.User;

import java.io.IOException;

public class CustomUserJsonSerializer extends StdSerializer<User> {
    public CustomUserJsonSerializer() {
        this(null);
    }

    public CustomUserJsonSerializer(Class<User> t) {
        super(t);
    }

    @Override
    public void serialize(User user, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("id", user.getId().toString());
        jsonGenerator.writeStringField("username", user.getUsername());
        jsonGenerator.writeStringField("firstName", user.getUsername());
        jsonGenerator.writeStringField("lastName", user.getLastName());
        jsonGenerator.writeStringField("avatarUrl", user.getAvatarUrl());
        jsonGenerator.writeObjectFieldStart("setting");
        jsonGenerator.writeNumberField("id", user.getSetting().getId());
        jsonGenerator.writeBooleanField("darkMode", user.getSetting().getDarkMode());
        jsonGenerator.writeEndObject();
        jsonGenerator.writeEndObject();
    }
}
