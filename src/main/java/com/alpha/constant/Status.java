package com.alpha.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.AttributeConverter;

/**
 * @author thanhvt
 * @created 06/06/2021 - 5:18 CH
 * @project vengeance
 * @since 1.0
 **/
public enum Status {
    INACTIVE(0),
    ACTIVE(1),
    REMOVED(2);

    private static final Map<Integer, Status> statusMap = new HashMap<>();

    static {
        for (Status status : Status.values()) {
            statusMap.put(status.value, status);
        }
    }

    private final int value;

    Status(int value) {
        this.value = value;
    }

    @JsonCreator
    public static Status fromValue(Integer integer) {
        if (integer == null) {
            return Status.INACTIVE;
        }
        Status gender = statusMap.get(integer);
        if (gender == null) {
            return Status.INACTIVE;
        }
        return gender;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    public static class StatusAttributeConverter implements AttributeConverter<Status, Integer> {

        @Override
        public Integer convertToDatabaseColumn(Status attribute) {
            if (attribute == null) {
                return Status.INACTIVE.value;
            }
            return attribute.value;
        }

        @Override
        public Status convertToEntityAttribute(Integer dbData) {
            return Status.fromValue(dbData);
        }
    }
}
