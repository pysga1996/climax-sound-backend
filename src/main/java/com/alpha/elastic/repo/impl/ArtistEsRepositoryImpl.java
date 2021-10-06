package com.alpha.elastic.repo.impl;

import com.alpha.elastic.model.ArtistEs;
import com.alpha.elastic.repo.ArtistEsRepositoryCustom;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;

/**
 * @author thanhvt
 * @created 10/7/2021 - 1:42 AM
 * @project vengeance
 * @since 1.0
 **/
@Repository
public class ArtistEsRepositoryImpl implements ArtistEsRepositoryCustom {

    private final ElasticsearchOperations elasticsearchRestTemplate;

    @Autowired
    public ArtistEsRepositoryImpl(
        ElasticsearchRestTemplate elasticsearchRestTemplate) {
        this.elasticsearchRestTemplate = elasticsearchRestTemplate;
    }

    @Override
    public Page<ArtistEs> findPageByName(String name, Pageable pageable) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        String[] tokens = name.split("\\s+");
        for (String token : tokens) {
            boolQueryBuilder.must(QueryBuilders.wildcardQuery("unaccentName", "*" + token + "*"));
        }
        SearchHits<ArtistEs> searchHits = this.elasticsearchRestTemplate
            .search(queryBuilder.withQuery(boolQueryBuilder).build(), ArtistEs.class);
        return SearchHitSupport.searchPageFor(searchHits, pageable).map(SearchHit::getContent);
    }
}
