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
public enum EntityStatus {
    INACTIVE(0),
    ACTIVE(1),
    REMOVED(2);

    private static final Map<Integer, EntityStatus> statusMap = new HashMap<>();

    static {
        for (EntityStatus status : EntityStatus.values()) {
            statusMap.put(status.value, status);
        }
    }

    private final int value;

    EntityStatus(int value) {
        this.value = value;
    }

    @JsonCreator
    public static EntityStatus fromValue(Integer integer) {
        if (integer == null) {
            return EntityStatus.INACTIVE;
        }
        EntityStatus gender = statusMap.get(integer);
        if (gender == null) {
            return EntityStatus.INACTIVE;
        }
        return gender;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    public static class StatusAttributeConverter implements AttributeConverter<EntityStatus, Integer> {

        @Override
        public Integer convertToDatabaseColumn(EntityStatus attribute) {
            if (attribute == null) {
                return EntityStatus.INACTIVE.value;
            }
            return attribute.value;
        }

        @Override
        public EntityStatus convertToEntityAttribute(Integer dbData) {
            return EntityStatus.fromValue(dbData);
        }
    }
}
