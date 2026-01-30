package com.github.fabriciolfj.study.configuration;

import com.github.fabriciolfj.study.clients.CarClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.service.registry.ImportHttpServices;

    @Configuration(proxyBeanMethods = false)
    @ImportHttpServices(CarClient.class)
    public class ClientCartRestConfig {

    /*private CustomHeaderInterceptor customHeaderInterceptor;
    private String url;

    public ClientCartRestConfig(final CustomHeaderInterceptor customHeaderInterceptor,
                                @Value("${car.host}") final String url) {
        this.customHeaderInterceptor = customHeaderInterceptor;
        this.url = url;
    }

    @Bean
    public CarClient restClient() {
        var restClient = RestClient.builder()
                .baseUrl(this.url)
                .defaultHeader("X-Api-Key", "minha-chave")
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .requestInterceptor(customHeaderInterceptor)
                .build();

        var adapter = RestClientAdapter.create(restClient);
        var proxy = HttpServiceProxyFactory.builderFor(adapter)
                .build();

        return proxy.createClient(CarClient.class);
    }*/
}
