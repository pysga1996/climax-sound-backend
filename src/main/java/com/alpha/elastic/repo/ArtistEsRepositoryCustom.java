package com.alpha.elastic.repo;

import com.alpha.elastic.model.ArtistEs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author thanhvt
 * @created 10/7/2021 - 1:42 AM
 * @project vengeance
 * @since 1.0
 **/
public interface ArtistEsRepositoryCustom {

    Page<ArtistEs> findPageByName(String name, Pageable pageable);

}
