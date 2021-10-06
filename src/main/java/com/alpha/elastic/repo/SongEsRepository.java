package com.alpha.elastic.repo;

import com.alpha.elastic.model.SongEs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author thanhvt
 * @created 10/6/2021 - 7:08 PM
 * @project vengeance
 * @since 1.0
 **/
public interface SongEsRepository extends ElasticsearchRepository<SongEs, Long>,
    SongEsRepositoryCustom {

    Page<SongEs> findFirst10ByUnaccentTitleContainingIgnoreCase(String name, Pageable pageable);
}
