package com.checkout.payment.gateway.validation;

import static com.checkout.payment.gateway.util.CardUtils.passesLuhnCheck;
import static org.apache.commons.lang3.StringUtils.isNumeric;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LuhnCheckValidator implements ConstraintValidator<LuhnCheck, String> {

  @Override
  public boolean isValid(String cardNumber, ConstraintValidatorContext context) {
    if (!isNumeric(cardNumber)) return false;
    return passesLuhnCheck(cardNumber);
  }

}
