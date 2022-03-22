package com.seeyon.ekds.engine.http;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.IdleConnectionEvictor;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;
/**
 * Created by liuwenping on 2021/7/26.
 *
 * @Author liuwenping
 */
@Configuration
public class HttpClientConfig {

    @Autowired
    private DefaultHttpPropConfig defaultHttpPropConfig;

    @Bean
    public PoolingHttpClientConnectionManager createPoolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager poolManager = new PoolingHttpClientConnectionManager();
        poolManager.setMaxTotal(defaultHttpPropConfig.getMaxTotal());
        poolManager.setDefaultMaxPerRoute(defaultHttpPropConfig.getDefaultMaxPerRoute());
        poolManager.setValidateAfterInactivity(defaultHttpPropConfig.getValidateAfterInactivity());
        return poolManager;
    }

    @Bean
    public HttpClient createHttpClient(PoolingHttpClientConnectionManager poolManager) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create().setConnectionManager(poolManager);
        httpClientBuilder.setKeepAliveStrategy((response, context) -> {
            HeaderElementIterator iterator = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (iterator.hasNext()) {
                HeaderElement headerElement = iterator.nextElement();
                String param = headerElement.getName();
                String value = headerElement.getValue();
                if (null != value && param.equalsIgnoreCase("timeout")) {
                    return Long.parseLong(value) * 1000;
                }
            }
            return 30 * 1000;
        });
        httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(defaultHttpPropConfig.getRetryCount(), false));
        return httpClientBuilder.build();
    }

    @Bean
    public RequestConfig createRequestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(defaultHttpPropConfig.getConnectionRequestTimeout())
                .setConnectTimeout(defaultHttpPropConfig.getConnectTimeout())
                .setSocketTimeout(defaultHttpPropConfig.getSocketTimeout())
                .build();
    }

    @Bean
    public IdleConnectionEvictor createIdleConnectionEvictor(PoolingHttpClientConnectionManager poolManager) {
        IdleConnectionEvictor idleConnectionEvictor = new IdleConnectionEvictor(poolManager, defaultHttpPropConfig.getIdleConTime(), TimeUnit.MILLISECONDS);
        return idleConnectionEvictor;
    }

    /**
     * 使用连接池管理连接
     *
     * @param httpClient
     * @return
     */
    @Bean
    public ClientHttpRequestFactory requestFactory(HttpClient httpClient) {
        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    /**
     * 使用HttpClient来初始化一个RestTemplate
     *
     * @param requestFactory
     * @return
     */
    @Bean("restTemplate")
    public RestTemplate restTemplate(ClientHttpRequestFactory requestFactory) {
        RestTemplate template = new RestTemplate(requestFactory);
        List<HttpMessageConverter<?>> messageConverterList = template.getMessageConverters();
        for (HttpMessageConverter mc : messageConverterList) {
            if (mc instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) mc).setDefaultCharset(Charset.forName("UTF-8"));
            }
        }
        return template;
    }

}
