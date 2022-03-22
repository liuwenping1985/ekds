package com.seeyon.ekds.engine.http;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by liuwenping on 2021/7/26.
 */
@Configuration
@PropertySource("classpath:${spring.profiles.active}/http.properties")
@ConfigurationProperties(prefix = "http")
@Data
public class DefaultHttpPropConfig {

    @Value("${ekds.http.maxTotal}")
    private Integer maxTotal = 100;

    @Value("${ekds.http.defaultMaxPerRoute}")
    private Integer defaultMaxPerRoute = 80;

    @Value("${ekds.http.validateAfterInactivity}")
    private Integer validateAfterInactivity = 1000;

    @Value("${ekds.http.retryCount}")
    private Integer retryCount = 3;

    @Value("${ekds.http.connectionRequestTimeout}")
    private Integer connectionRequestTimeout = 10000;

    @Value("${ekds.http.connectionTimeout}")
    private Integer connectTimeout = 10000;

    @Value("${ekds.http.socketTimeout}")
    private Integer socketTimeout = 5000;

    @Value("${ekds.http.waitTime}")
    private Integer waitTime = 30000;

    @Value("${ekds.http.idleConTime}")
    private Integer idleConTime = 3;
}
