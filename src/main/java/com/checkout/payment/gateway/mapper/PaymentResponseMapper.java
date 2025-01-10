package com.checkout.payment.gateway.mapper;

import static com.checkout.payment.gateway.enums.PaymentStatus.AUTHORIZED;
import static com.checkout.payment.gateway.enums.PaymentStatus.DECLINED;
import static com.checkout.payment.gateway.util.CardUtils.getLastFour;

import com.checkout.payment.gateway.client.acquiringbank.docker.model.AcquiringBankPaymentResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import java.util.UUID;

public class PaymentResponseMapper {

  public static PostPaymentResponse mapToPostPaymentResponse(
      UUID paymentId,
      PostPaymentRequest postPaymentRequest,
      AcquiringBankPaymentResponse acquiringBankPaymentResponse) {
    return new PostPaymentResponse(paymentId,
        acquiringBankPaymentResponse.getAuthorized() ? AUTHORIZED : DECLINED,
        getLastFour(postPaymentRequest.getCardNumber()),
        postPaymentRequest.getExpiryMonth(),
        postPaymentRequest.getExpiryYear(),
        postPaymentRequest.getCurrency(),
        postPaymentRequest.getAmount());
  }
}
