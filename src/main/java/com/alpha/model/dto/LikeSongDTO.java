package com.alpha.model.dto;

import lombok.Data;

/**
 * @author thanhvt
 * @created 26/07/2021 - 11:15 CH
 * @project vengeance
 * @since 1.0
 **/
@Data
public class LikeSongDTO {

    private Long songId;

    private Boolean isLiked;
}
