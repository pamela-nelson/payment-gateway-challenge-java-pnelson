package com.checkout.payment.gateway.exception;

public class AcquiringBankClientException extends RuntimeException{
  public AcquiringBankClientException(String message) {
    super(message);
  }
}
