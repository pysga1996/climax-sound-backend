package com.alpha.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import lombok.Data;

/**
 * @author thanhvt
 * @created 04/09/2021 - 3:35 CH
 * @project vengeance
 * @since 1.0
 **/
@Data
public class AlbumUpdateDTO {

    private Long songId;

    private Short ordinalNumber;

    private UpdateMode mode;

    @JsonFormat(shape = Shape.STRING)
    public enum UpdateMode {
        CREATE,UPDATE,DELETE,VIEW
    }
}
