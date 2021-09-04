package com.alpha.model.dto;

import lombok.Data;

/**
 * @author thanhvt
 * @created 02/09/2021 - 6:29 CH
 * @project vengeance
 * @since 1.0
 **/
@Data
public class SongSearchDTO {

    private Long artistId;

    private Long albumId;

    private Long playlistId;

    private String usernameFavorite;

    private String username;

    private String phrase;

}
