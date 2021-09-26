package com.alpha.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author thanhvt
 * @created 9/26/2021 - 2:26 PM
 * @project vengeance
 * @since 1.0
 **/
public enum CommentType {

    SONG,
    ALBUM,
    ARTIST;

    @JsonCreator
    public static CommentType fromValue(String val) {
        return CommentType.valueOf(val);
    }

    @JsonValue
    public String getValue() {
        return this.name();
    }
}
