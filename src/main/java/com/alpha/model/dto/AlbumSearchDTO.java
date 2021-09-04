package com.alpha.model.dto;

import lombok.Data;

/**
 * @author thanhvt
 * @created 03/09/2021 - 9:59 CH
 * @project vengeance
 * @since 1.0
 **/
@Data
public class AlbumSearchDTO {

    private Long artistId;

    private Long albumId;

    private String usernameFavorite;

    private String username;

    private String phrase;

}
