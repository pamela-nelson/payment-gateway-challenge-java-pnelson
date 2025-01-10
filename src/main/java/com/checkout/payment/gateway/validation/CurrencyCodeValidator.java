package com.checkout.payment.gateway.validation;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Currency;
import java.util.List;

public class CurrencyCodeValidator implements ConstraintValidator<CurrencyCode, String> {

  private final List<Currency> SUPPORTED_CURRENCIES = List.of(
      Currency.getInstance("GBP"), // POUNDS
      Currency.getInstance("USD"), // DOLLARS
      Currency.getInstance("EUR")  // EUROS
  );

  @Override
  public boolean isValid(String currencyCode, ConstraintValidatorContext constraintValidatorContext) {
    if (isEmpty(currencyCode)) return false;
    try {
      var currency = Currency.getInstance(currencyCode.toUpperCase());
      return SUPPORTED_CURRENCIES.contains(currency);
    } catch (Exception e) {
      return false;
    }
  }
}
