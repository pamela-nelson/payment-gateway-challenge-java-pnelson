package com.checkout.payment.gateway.configuration;

import com.checkout.payment.gateway.client.acquiringbank.AcquiringBankClient;
import com.checkout.payment.gateway.client.acquiringbank.docker.DockerAcquiringBankClient;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationConfiguration {

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder
        .setConnectTimeout(Duration.ofMillis(10000))
        .setReadTimeout(Duration.ofMillis(10000))
        .build();
  }

  @Bean
  public AcquiringBankClient acquiringBankClient(RestTemplate restTemplate,
      @Value("${acquiringBank.client.baseUrl}") String acquiringBankBaseUrl) {
    return new DockerAcquiringBankClient(restTemplate, acquiringBankBaseUrl);
  }
}
