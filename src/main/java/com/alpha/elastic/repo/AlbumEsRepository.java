package com.alpha.elastic.repo;

import com.alpha.elastic.model.AlbumEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author thanhvt
 * @created 10/6/2021 - 7:13 PM
 * @project vengeance
 * @since 1.0
 **/
@Repository
public interface AlbumEsRepository extends ElasticsearchRepository<AlbumEs, Long> {

}
