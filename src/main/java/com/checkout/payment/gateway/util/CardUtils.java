package com.checkout.payment.gateway.util;

import static java.lang.Character.getNumericValue;
import static java.time.LocalDate.now;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class CardUtils {

  public static String getLastFour(String cardNumber) {
    if (isEmpty(cardNumber) || cardNumber.length() < 4)
      throw new IllegalArgumentException("Card Number too short to get last four");

    return cardNumber.substring(cardNumber.length() - 4);
  }

  public static boolean passesLuhnCheck(String cardNumber) {
    var sum = 0;
    var isEvenPositionDigit = false;
    for (int i = cardNumber.length() - 1; i >= 0; i--) {
      var digit = getNumericValue(cardNumber.charAt(i));
      if (isEvenPositionDigit) {
        digit *= 2;
      }
      sum += digit / 10;
      sum += digit % 10;
      isEvenPositionDigit = !isEvenPositionDigit;
    }
    return sum % 10 == 0;
  }

  public static boolean isNotExpired(int expiryMonth, int expiryYear) {
    var currentMonth = now().getMonthValue();
    var currentYear = now().getYear();

    if (expiryYear > currentYear) {
      return true;
    }

    return expiryYear == currentYear && expiryMonth >= currentMonth;
  }

}
