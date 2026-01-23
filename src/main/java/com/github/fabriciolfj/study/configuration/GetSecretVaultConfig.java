package com.github.fabriciolfj.study.configuration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class GetSecretVaultConfig {

    @Value("${database.username}")
    private String dbUsername;

    @Value("${database.password}")
    private String dbPassword;

    @Value("${api.key}")
    private String apiKey;

    @PostConstruct
    public void printSecrets() {
        System.out.println("=================================");
        System.out.println("SECRETS CARREGADOS DO VAULT:");
        System.out.println("Database Username: " + dbUsername);
        System.out.println("Database Password: " + dbPassword);
        System.out.println("API Key: " + apiKey);
        System.out.println("=================================");
    }

}
