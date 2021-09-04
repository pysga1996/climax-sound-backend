package com.alpha.model.dto;

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

    private Short order;

    private UpdateMode mode;

    public enum UpdateMode {
        CREATE,DELETE
    }
}
