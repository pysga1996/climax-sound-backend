package com.alpha.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author thanhvt
 * @created 9/26/2021 - 2:26 PM
 * @project vengeance
 * @since 1.0
 **/
public enum EntityType {

    SONG,
    ALBUM,
    ARTIST;

    @JsonCreator
    public static EntityType fromValue(String val) {
        return EntityType.valueOf(val);
    }

    @JsonValue
    public String getValue() {
        return this.name();
    }
}
