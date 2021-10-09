package com.alpha.config.general;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @author thanhvt
 * @created 10/6/2021 - 7:08 PM
 * @project vengeance
 * @since 1.0
 **/
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.alpha.elastic.repo")
@ComponentScan(basePackages = { "com.alpha.elastic.model" })
public class ElasticSearchConfig {

    private final RestHighLevelClient restHighLevelClient;

    @Autowired
    public ElasticSearchConfig(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

//    @Bean
//    public RestHighLevelClient client() {
//        ClientConfiguration clientConfiguration
//            = ClientConfiguration.builder()
//            .connectedTo("localhost:9200")
//            .build();
//
//        return RestClients.create(clientConfiguration).rest();
//    }


//    @Bean
//    public ElasticsearchOperations elasticsearchTemplate() {
//        return new ElasticsearchRestTemplate(restHighLevelClient, );
//    }
}
