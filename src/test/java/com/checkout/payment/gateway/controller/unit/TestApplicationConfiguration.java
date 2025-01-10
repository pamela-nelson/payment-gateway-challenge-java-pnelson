package com.checkout.payment.gateway.controller.unit;

import com.checkout.payment.gateway.client.acquiringbank.AcquiringBankClient;
import com.checkout.payment.gateway.controller.utils.MockAcquiringBankClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestApplicationConfiguration {

  @Bean
  @Primary
  public AcquiringBankClient mockAcquiringBankClient() {
    return new MockAcquiringBankClient();
  }
}
