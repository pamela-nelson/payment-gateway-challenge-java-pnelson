package com.checkout.payment.gateway.controller.utils;

import static com.checkout.payment.gateway.enums.PaymentStatus.AUTHORIZED;
import static java.util.UUID.randomUUID;

import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;

public class TestUtils {

  //Test Cards
  public static final String AUTHORIZED_CARD_NUMBER = "2222405343248877";
  public static final String AUTHORIZED_LAST_FOUR = "8877";
  private static final String AUTHORIZED_CVV = "123";
  private static final int AUTHORIZED_MONTH = 4;
  private static final int AUTHORIZED_YEAR = 2025;
  private static final String POUND_CODE = "GBP";
  private static final int AUTHORIZED_AMOUNT = 100;

  public static final String DECLINED_CARD_NUMBER = "2222405343248117";
  private static final String DECLINED_CVV = "456";
  private static final int DECLINED_MONTH = 1;
  private static final int DECLINED_YEAR = 2026;
  private static final String DOLLAR_CODE = "USD";
  private static final int DECLINED_AMOUNT = 60000;

  public static final String CARD_NUMBER_FAILS_LUHN_CHECK = "2222405343248112";


  public static PostPaymentResponse createAuthorisedPaymentResponse() {
    return new PostPaymentResponse(
        randomUUID(),
        AUTHORIZED,
        AUTHORIZED_LAST_FOUR,
        AUTHORIZED_MONTH,
        AUTHORIZED_YEAR,
        POUND_CODE,
        AUTHORIZED_AMOUNT);
  }

  public static PostPaymentRequest createAuthorisedPostPaymentRequest() {
    return new PostPaymentRequest(
        AUTHORIZED_CARD_NUMBER,
        AUTHORIZED_MONTH,
        AUTHORIZED_YEAR,
        AUTHORIZED_CVV,
        DOLLAR_CODE,
        AUTHORIZED_AMOUNT);
  }

  public static PostPaymentRequest createDeclinedPostPaymentRequest() {
    return new PostPaymentRequest(
        DECLINED_CARD_NUMBER,
        DECLINED_MONTH,
        DECLINED_YEAR,
        DECLINED_CVV,
        DOLLAR_CODE,
        DECLINED_AMOUNT);
  }
}
