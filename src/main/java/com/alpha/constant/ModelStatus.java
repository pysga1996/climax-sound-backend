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
public enum ModelStatus {
    INACTIVE(0),
    ACTIVE(1),
    REMOVED(2);

    private static final Map<Integer, ModelStatus> statusMap = new HashMap<>();

    static {
        for (ModelStatus status : ModelStatus.values()) {
            statusMap.put(status.value, status);
        }
    }

    private final int value;

    ModelStatus(int value) {
        this.value = value;
    }

    @JsonCreator
    public static ModelStatus fromValue(Integer integer) {
        if (integer == null) {
            return ModelStatus.INACTIVE;
        }
        ModelStatus gender = statusMap.get(integer);
        if (gender == null) {
            return ModelStatus.INACTIVE;
        }
        return gender;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    public static class StatusAttributeConverter implements AttributeConverter<ModelStatus, Integer> {

        @Override
        public Integer convertToDatabaseColumn(ModelStatus attribute) {
            if (attribute == null) {
                return ModelStatus.INACTIVE.value;
            }
            return attribute.value;
        }

        @Override
        public ModelStatus convertToEntityAttribute(Integer dbData) {
            return ModelStatus.fromValue(dbData);
        }
    }
}
