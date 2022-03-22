package com.seeyon.ekds.datasource.es;

import com.seeyon.ekds.datasource.es.conversions.GenericDataHolderToMap;
import com.seeyon.ekds.datasource.es.conversions.MapToGenericDataHolder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;

import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * es的配置
 * Created by liuwenping on 2021/6/25.
 * @author liuwenping
 */
@Configuration
@PropertySource("classpath:${spring.profiles.active}/es.properties")
public class EsRestClientConfig extends AbstractElasticsearchConfiguration {

    @Value("${es.url}")
    private String url;

    @Value("${es.port}")
    private String port;

    @Override
    public RestHighLevelClient elasticsearchClient() {
        String conUrl = null;
        if (url != null && port != null) {
            conUrl = url + ":" + port;
        } else {
            conUrl = "localhost:9200";
        }
        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(conUrl)
                .build();
        return RestClients.create(clientConfiguration).rest();
    }

    @Bean(name = {"elasticsearchOperations", "elasticsearchRestTemplate"})
    public ElasticsearchRestTemplate elasticsearchRestTemplate() throws UnknownHostException {
        return new ElasticsearchRestTemplate(elasticsearchClient());
    }


    @Bean
    @Override
    public ElasticsearchCustomConversions elasticsearchCustomConversions() {
        return new ElasticsearchCustomConversions(
                Arrays.asList(new GenericDataHolderToMap(), new MapToGenericDataHolder()));
    }
    @Bean
    public ElasticsearchConverter elasticsearchConverter() {
        return new MappingElasticsearchConverter(elasticsearchMappingContext());
    }
    @Bean
    public SimpleElasticsearchMappingContext elasticsearchMappingContext() {
        return new SimpleElasticsearchMappingContext();
    }

}
