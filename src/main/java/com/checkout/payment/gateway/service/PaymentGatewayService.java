package com.checkout.payment.gateway.service;

import static com.checkout.payment.gateway.mapper.PaymentResponseMapper.mapToPostPaymentResponse;
import static com.checkout.payment.gateway.util.CardUtils.getLastFour;
import static java.util.UUID.randomUUID;
import static org.slf4j.LoggerFactory.getLogger;

import com.checkout.payment.gateway.client.acquiringbank.AcquiringBankClient;
import com.checkout.payment.gateway.client.acquiringbank.docker.exception.UnsupportedCardException;
import com.checkout.payment.gateway.client.acquiringbank.docker.model.AcquiringBankPaymentResponse;
import com.checkout.payment.gateway.exception.AcquiringBankClientException;
import com.checkout.payment.gateway.exception.EntityNotFoundException;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatewayService {

  private static final Logger LOG = getLogger(PaymentGatewayService.class);

  private final PaymentsRepository paymentsRepository;
  private final AcquiringBankClient acquiringBankClient;

  @Autowired
  public PaymentGatewayService(
      PaymentsRepository paymentsRepository,
      AcquiringBankClient acquiringBankClient) {
    this.paymentsRepository = paymentsRepository;
    this.acquiringBankClient = acquiringBankClient;
  }

  public PostPaymentResponse getPaymentById(UUID id) {
    LOG.debug("Requesting access to payment. paymentId={}", id);
    return paymentsRepository.get(id)
        .orElseThrow(() -> new EntityNotFoundException("Payment not found. paymentId=" + id));
  }

  public PostPaymentResponse processPayment(PostPaymentRequest paymentRequest) {
    var paymentId = randomUUID();
    LOG.info("Preparing payment for processing by acquiring bank. paymentId={} lastFour={}",
        paymentId, getLastFour(paymentRequest.getCardNumber()));

    AcquiringBankPaymentResponse paymentResult;
    try {
      paymentResult = acquiringBankClient.processPayment(
          paymentRequest.getCardNumber(),
          paymentRequest.getExpiryDate(),
          paymentRequest.getCvv(),
          paymentRequest.getCurrency(),
          paymentRequest.getAmount());
    } catch (UnsupportedCardException e) {
      LOG.warn("Card not supported by acquiring bank. paymentId={} lastFour={}",
          paymentId, getLastFour(paymentRequest.getCardNumber()));
      throw e;
    } catch (Exception e) {
      LOG.error("Error processing payment with acquiring bank. paymentId={} lastFour={}",
          paymentId, getLastFour(paymentRequest.getCardNumber()), e);
      throw new AcquiringBankClientException("Error processing payment");
    }
    LOG.info("Payment processed by acquiring bank. paymentId={} lastFour={} isAuthorised={}",
        paymentId, getLastFour(paymentRequest.getCardNumber()), paymentResult.getAuthorized());
    var paymentResponse = mapToPostPaymentResponse(paymentId, paymentRequest, paymentResult);
    paymentsRepository.add(paymentResponse);
    return paymentResponse;
  }
}
