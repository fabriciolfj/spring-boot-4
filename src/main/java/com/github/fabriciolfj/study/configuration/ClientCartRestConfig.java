package com.github.fabriciolfj.study.configuration;

import com.github.fabriciolfj.study.clients.CarClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.service.registry.ImportHttpServices;


@Configuration(proxyBeanMethods = false)
@ImportHttpServices(group = "cars", types = {CarClient.class})
public class ClientCartRestConfig {

    @Bean
    RestClientHttpServiceGroupConfigurer configurer(
            ClientHttpRequestFactory factory) {
        return groups -> groups
                .forEachClient((group, builder) -> builder
                        .requestFactory(factory));
    }

}
