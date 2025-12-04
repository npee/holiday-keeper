package io.npee.holidaykeeper.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ExternalApiConfig {

    @Bean
    public RestClient nagerRestClient(RestClient.Builder builder) {
        return builder.baseUrl("https://date.nager.at/api/v3")
                .build();
    }

}
