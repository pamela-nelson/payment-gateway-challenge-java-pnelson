package com.checkout.payment.gateway.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CurrentOrFutureYearValidator implements ConstraintValidator<CurrentOrFutureYear, Integer> {

  @Override
  public boolean isValid(Integer expiryYear, ConstraintValidatorContext context) {
    if (expiryYear == null) return false;

    var currentYear = java.time.Year.now().getValue();
    return expiryYear >= currentYear;
  }
}
