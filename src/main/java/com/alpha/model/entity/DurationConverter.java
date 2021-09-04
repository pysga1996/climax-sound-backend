package com.alpha.model.entity;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Converter(autoApply = true)
public class DurationConverter implements AttributeConverter<Duration, Short> {

    @Override
    public Short convertToDatabaseColumn(Duration attribute) {
        if (attribute == null) return 0;
        return (short) attribute.getSeconds();
    }

    @Override
    public Duration convertToEntityAttribute(Short duration) {
        return Duration.of(duration, ChronoUnit.SECONDS);
    }
}