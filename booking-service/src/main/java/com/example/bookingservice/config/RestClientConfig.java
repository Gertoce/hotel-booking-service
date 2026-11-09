package com.example.bookingservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    @LoadBalanced
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestClient hotelServiceClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder
                .baseUrl("http://hotel-management-service")
                .build();
    }
}