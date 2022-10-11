package com.alpha.elastic.repo;

import com.alpha.elastic.model.AlbumEs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author thanhvt
 * @created 10/7/2021 - 2:31 AM
 * @project vengeance
 * @since 1.0
 **/
public interface AlbumEsRepositoryCustom {

    Page<AlbumEs> findPageByName(String name, Pageable pageable);

}
