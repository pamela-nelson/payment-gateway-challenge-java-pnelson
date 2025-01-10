package com.checkout.payment.gateway.controller.utils;

import static com.checkout.payment.gateway.controller.utils.TestUtils.AUTHORIZED_CARD_NUMBER;
import static com.checkout.payment.gateway.controller.utils.TestUtils.DECLINED_CARD_NUMBER;
import static java.util.UUID.randomUUID;

import com.checkout.payment.gateway.client.acquiringbank.AcquiringBankClient;
import com.checkout.payment.gateway.client.acquiringbank.docker.exception.UnsupportedCardException;
import com.checkout.payment.gateway.client.acquiringbank.docker.model.AcquiringBankPaymentResponse;

public class MockAcquiringBankClient implements AcquiringBankClient {

  @Override
  public AcquiringBankPaymentResponse processPayment(String cardNumber, String expiryDate,
      String cvv, String currency, int amount) {
    if (cardNumber.equals(AUTHORIZED_CARD_NUMBER))
      return new AcquiringBankPaymentResponse(true, randomUUID());
    else if (cardNumber.equals(DECLINED_CARD_NUMBER))
      return new AcquiringBankPaymentResponse(false, null);
    else
      throw new UnsupportedCardException("Payment Card is not supported");
  }
}
