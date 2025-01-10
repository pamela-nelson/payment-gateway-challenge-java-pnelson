package com.checkout.payment.gateway.client.acquiringbank;

import com.checkout.payment.gateway.client.acquiringbank.docker.model.AcquiringBankPaymentResponse;

/**
 * Interface for acquiring banks to implement for payments
 */
public interface AcquiringBankClient {

  AcquiringBankPaymentResponse processPayment(
      String cardNumber,
      String expiryDate,
      String cvv,
      String currency,
      int amount);

}
