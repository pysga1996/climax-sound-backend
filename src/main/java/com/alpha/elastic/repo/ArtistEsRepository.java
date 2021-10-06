package com.alpha.elastic.repo;

import com.alpha.elastic.model.ArtistEs;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author thanhvt
 * @created 10/6/2021 - 7:12 PM
 * @project vengeance
 * @since 1.0
 **/
@Repository
public interface ArtistEsRepository extends ElasticsearchRepository<ArtistEs, Long> {

    List<ArtistEs> findFirst10ByUnaccentNameContainingIgnoreCase(String name);

}
