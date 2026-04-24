package com.github.fabriciolfj.study.configuration;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@ConfigurationProperties(prefix = "app.http-client")
public class HttpClientConfig {

    // Pool geral
    private int maxTotal = 200;
    private int defaultMaxPerRoute = 50;

    // Timeouts
    private Duration connectTimeout = Duration.ofSeconds(2);
    private Duration socketTimeout = Duration.ofSeconds(5);
    private Duration connectionRequestTimeout = Duration.ofSeconds(3);
    private Duration connectionTimeToLive = Duration.ofSeconds(60);
    private Duration evictIdleAfter = Duration.ofSeconds(30);

    @Bean
    public CloseableHttpClient apacheHttpClient() {
        // 1. Connection pool
        PoolingHttpClientConnectionManager connManager =
                PoolingHttpClientConnectionManagerBuilder.create()
                        .setMaxConnTotal(maxTotal)
                        .setMaxConnPerRoute(defaultMaxPerRoute)
                        .setConnectionTimeToLive(
                                TimeValue.of(connectionTimeToLive.toMillis(), TimeUnit.MILLISECONDS))
                        .setDefaultConnectionConfig(
                                ConnectionConfig.custom()
                                        .setConnectTimeout(
                                                Timeout.of(connectTimeout.toMillis(), TimeUnit.MILLISECONDS))
                                        .setSocketTimeout(
                                                Timeout.of(socketTimeout.toMillis(), TimeUnit.MILLISECONDS))
                                        .build())
                        .build();

        // 2. Request config (timeouts por request)
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(
                        Timeout.of(connectionRequestTimeout.toMillis(), TimeUnit.MILLISECONDS))
                .build();

        return HttpClients.custom()
                .setConnectionManager(connManager)
                .setDefaultRequestConfig(requestConfig)
                // 3. Evict conexões ociosas/expiradas em background
                .evictExpiredConnections()
                .evictIdleConnections(
                        TimeValue.of(evictIdleAfter.toMillis(), TimeUnit.MILLISECONDS))
                .build();
    }

    @Bean
    public ClientHttpRequestFactory httpRequestFactory(CloseableHttpClient httpClient) {
        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }
}