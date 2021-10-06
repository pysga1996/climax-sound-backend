package com.alpha.elastic.repo;

import com.alpha.elastic.model.AlbumEs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author thanhvt
 * @created 10/6/2021 - 7:13 PM
 * @project vengeance
 * @since 1.0
 **/
public interface AlbumEsRepository extends ElasticsearchRepository<AlbumEs, Long>,
    AlbumEsRepositoryCustom {

    Page<AlbumEs> findFirst10ByUnaccentTitleContainingIgnoreCase(String name, Pageable pageable);
}
