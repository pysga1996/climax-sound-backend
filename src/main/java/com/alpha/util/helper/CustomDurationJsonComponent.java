package com.alpha.util.helper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.time.Duration;

@JsonComponent
public class CustomDurationJsonComponent {
    public static class CustomDurationJsonDeserializer extends JsonDeserializer<Duration> {
        @Override
        public Duration deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            Long second = jsonParser.readValueAs(Long.TYPE);
            return Duration.ofSeconds(second);
        }
    }

    public static class CustomDurationJsonSerializer extends JsonSerializer<Duration> {
        @Override
        public void serialize(Duration duration, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeNumber(duration.getSeconds());
        }
    }
}
