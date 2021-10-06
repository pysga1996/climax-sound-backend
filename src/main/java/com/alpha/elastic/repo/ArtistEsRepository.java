package com.alpha.elastic.repo;

import com.alpha.elastic.model.ArtistEs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author thanhvt
 * @created 10/6/2021 - 7:12 PM
 * @project vengeance
 * @since 1.0
 **/
public interface ArtistEsRepository extends ElasticsearchRepository<ArtistEs, Long>,
    ArtistEsRepositoryCustom {

    Page<ArtistEs> findFirst10ByUnaccentNameContainingIgnoreCase(String name, Pageable pageable);

}
