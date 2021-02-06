package com.alpha.util.helper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.alpha.model.dto.UserDTO;

import java.io.IOException;

public class CustomUserJsonSerializer extends StdSerializer<UserDTO> {
    public CustomUserJsonSerializer() {
        this(null);
    }

    public CustomUserJsonSerializer(Class<UserDTO> t) {
        super(t);
    }

    @Override
    public void serialize(UserDTO user, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
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
