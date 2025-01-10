package com.checkout.payment.gateway.client.acquiringbank.docker;

import com.checkout.payment.gateway.client.acquiringbank.AcquiringBankClient;
import com.checkout.payment.gateway.client.acquiringbank.docker.exception.UnsupportedCardException;
import com.checkout.payment.gateway.client.acquiringbank.docker.model.AcquiringBankPaymentRequest;
import com.checkout.payment.gateway.client.acquiringbank.docker.model.AcquiringBankPaymentResponse;
import org.springframework.web.client.RestTemplate;

/**
 * Client to communicate with the Docker Payment simulator
 */
public class DockerAcquiringBankClient implements AcquiringBankClient {

  private static final String PAYMENTS = "/payments";

  private final RestTemplate restTemplate;
  private final String baseUrl;

  public DockerAcquiringBankClient(RestTemplate restTemplate, String baseUrl) {
    this.restTemplate = restTemplate;
    this.baseUrl = baseUrl;
  }

  public AcquiringBankPaymentResponse processPayment(
      String cardNumber,
      String expiryDate,
      String cvv,
      String currency,
      int amount) {
    try {
      return restTemplate.postForObject(
          baseUrl + PAYMENTS,
          new AcquiringBankPaymentRequest(cardNumber, expiryDate, cvv, currency, amount),
          AcquiringBankPaymentResponse.class);
    } catch (Exception e) {
      if (e.getMessage().contains("not supported"))
        throw new UnsupportedCardException("Payment Card is not supported");
      else throw e;
    }
  }
}
