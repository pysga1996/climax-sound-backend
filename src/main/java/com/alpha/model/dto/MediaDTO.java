package com.alpha.model.dto;

import com.alpha.constant.EntityType;
import java.util.Collection;

/**
 * @author thanhvt
 * @created 9/26/2021 - 9:47 PM
 * @project vengeance
 * @since 1.0
 **/
public interface MediaDTO {

    Long getId();

    Collection<ArtistDTO> getArtists();

    EntityType getType();

    void setLiked(Boolean isLiked);
}
