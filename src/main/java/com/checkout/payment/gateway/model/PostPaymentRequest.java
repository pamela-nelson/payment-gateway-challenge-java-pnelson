package com.checkout.payment.gateway.model;

import static com.checkout.payment.gateway.util.RegexPatterns.ALL_NUMERIC_DIGITS;
import static java.lang.String.format;

import com.checkout.payment.gateway.validation.CurrencyCode;
import com.checkout.payment.gateway.validation.CurrentOrFutureYear;
import com.checkout.payment.gateway.validation.FutureExpiryDate;
import com.checkout.payment.gateway.validation.LuhnCheck;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

@FutureExpiryDate(message = "Expiry date must be in the future")
public class PostPaymentRequest implements Serializable {

  @NotNull(message = "Card number is required")
  @Size(min = 14, max = 19, message = "Card number must be between 14 and 19 characters long")
  @Pattern(regexp = ALL_NUMERIC_DIGITS, message = "Card number must only contain numeric characters")
  @LuhnCheck
  private String cardNumber;

  @NotNull(message = "Expiry month is required")
  @Min(value = 1, message = "Expiry month must be between 1 and 12")
  @Max(value = 12, message = "Expiry month must be between 1 and 12")
  private int expiryMonth;

  @NotNull(message = "Expiry Year is required")
  @CurrentOrFutureYear
  private int expiryYear;

  @NotNull(message = "Currency is required")
  @Size(min = 3, max = 3, message = "Currency must be exactly 3 characters long")
  @CurrencyCode
  private String currency;

  @NotNull(message = "Amount is required")
  @Min(value = 1, message = "Amount must be greater than 0")
  private int amount;

  @NotNull(message = "CVV is required")
  @Size(min = 3, max = 4, message = "CVV must be 3 or 4 characters long")
  @Pattern(regexp = ALL_NUMERIC_DIGITS, message = "CVV must only contain numeric characters")
  private String cvv;

  public PostPaymentRequest() {

  }

  public PostPaymentRequest(
      String cardNumber,
      int expiryMonth,
      int expiryYear,
      String cvv,
      String currency,
      int amount) {
    this.cardNumber = cardNumber;
    this.expiryMonth = expiryMonth;
    this.expiryYear = expiryYear;
    this.cvv = cvv;
    this.currency = currency;
    this.amount = amount;
  }

  public String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public int getExpiryMonth() {
    return expiryMonth;
  }

  public void setExpiryMonth(int expiryMonth) {
    this.expiryMonth = expiryMonth;
  }

  public int getExpiryYear() {
    return expiryYear;
  }

  public void setExpiryYear(int expiryYear) {
    this.expiryYear = expiryYear;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public String getCvv() {
    return cvv;
  }

  public void setCvv(String cvv) {
    this.cvv = cvv;
  }

  public String getExpiryDate() {
    return format("%02d/%d", expiryMonth, expiryYear);
  }

  @Override
  public String toString() {
    return "PostPaymentRequest{" +
        "cardNumberLastFour=" + cardNumber +
        ", expiryMonth=" + expiryMonth +
        ", expiryYear=" + expiryYear +
        ", currency='" + currency + '\'' +
        ", amount=" + amount +
        ", cvv=" + cvv +
        '}';
  }
}
