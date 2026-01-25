package com.github.fabriciolfj.study.configuration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomHeaderInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution) throws IOException {

        HttpRequest modifiedRequest = new HttpRequestWrapper(request) {
            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.putAll(super.getHeaders());

                // Adiciona seus headers
                headers.set("X-Api-Key", "minha-api-key");
                headers.set("X-Tenant-Id", getTenantId());
                headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

                return headers;
            }
        };

        return execution.execute(modifiedRequest, body);
    }

    private String getTenantId() {
        // Pegar do contexto, security context, etc
        return "tenant-123";
    }
}