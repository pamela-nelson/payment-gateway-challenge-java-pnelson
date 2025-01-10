package com.checkout.payment.gateway.validation;

import static com.checkout.payment.gateway.util.CardUtils.isNotExpired;

import com.checkout.payment.gateway.model.PostPaymentRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FutureExpiryDateValidator implements ConstraintValidator<FutureExpiryDate, PostPaymentRequest> {


  @Override
  public boolean isValid(PostPaymentRequest postPaymentRequest, ConstraintValidatorContext context) {
    if (postPaymentRequest == null) {
      return true;
    }

    return isNotExpired(postPaymentRequest.getExpiryMonth(), postPaymentRequest.getExpiryYear());
  }
}
