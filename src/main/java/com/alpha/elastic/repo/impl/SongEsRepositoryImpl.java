package com.alpha.elastic.repo.impl;

import com.alpha.elastic.model.SongEs;
import com.alpha.elastic.repo.SongEsRepositoryCustom;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;

/**
 * @author thanhvt
 * @created 10/7/2021 - 2:31 AM
 * @project vengeance
 * @since 1.0
 **/
@Repository
public class SongEsRepositoryImpl implements SongEsRepositoryCustom {

    private final ElasticsearchOperations elasticsearchRestTemplate;

    @Autowired
    public SongEsRepositoryImpl(
        ElasticsearchOperations elasticsearchRestTemplate) {
        this.elasticsearchRestTemplate = elasticsearchRestTemplate;
    }

    @Override
    public Page<SongEs> findPageByName(String name, Pageable pageable) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        String[] tokens = name.split("\\s+");
        for (String token : tokens) {
            boolQueryBuilder.must(QueryBuilders.wildcardQuery("unaccentTitle", "*" + token + "*"));
        }
        SearchHits<SongEs> searchHits = this.elasticsearchRestTemplate
            .search(queryBuilder.withQuery(boolQueryBuilder).build(), SongEs.class);
        return SearchHitSupport.searchPageFor(searchHits, pageable).map(SearchHit::getContent);
    }
}
