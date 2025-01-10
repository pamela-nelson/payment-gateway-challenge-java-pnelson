package com.checkout.payment.gateway.client.acquiringbank.docker.exception;

public class UnsupportedCardException extends RuntimeException{
  public UnsupportedCardException(String message) {
    super(message);
  }
}
