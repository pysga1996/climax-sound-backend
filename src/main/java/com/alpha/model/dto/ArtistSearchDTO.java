package com.alpha.model.dto;

import lombok.Data;

/**
 * @author thanhvt
 * @created 31/08/2021 - 11:18 CH
 * @project vengeance
 * @since 1.0
 **/
@Data
public class ArtistSearchDTO {

    private String phrase;

    private Long songId;

    private Long albumId;

    private String usernameFavorite;

    private String username;

}
