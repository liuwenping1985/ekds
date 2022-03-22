package com.seeyon.ekds.datasource.es;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Created by liuwenping on 2021/7/15.
 */
@Configuration
@EnableElasticsearchRepositories(
        basePackages = "com.seeyon.ekds.dao.es.repository.*"
)
public class EsRepositoriesConfig {
}
