package com.checkout.payment.gateway.controller.unit;

import static com.checkout.payment.gateway.controller.utils.TestUtils.AUTHORIZED_CARD_NUMBER;
import static com.checkout.payment.gateway.controller.utils.TestUtils.AUTHORIZED_LAST_FOUR;
import static com.checkout.payment.gateway.controller.utils.TestUtils.CARD_NUMBER_FAILS_LUHN_CHECK;
import static com.checkout.payment.gateway.util.CardUtils.getLastFour;
import static com.checkout.payment.gateway.util.CardUtils.isNotExpired;
import static com.checkout.payment.gateway.util.CardUtils.passesLuhnCheck;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CardUtilsTest {

  @Test
  void testGetLastFour_ValidCardNumber() {
    assertEquals(AUTHORIZED_LAST_FOUR, getLastFour(AUTHORIZED_CARD_NUMBER));
  }

  @Test
  void testGetLastFour_CardNumberTooShort() {
    var exception = assertThrows(IllegalArgumentException.class, () -> getLastFour("123"));
    assertEquals("Card Number too short to get last four", exception.getMessage());
  }

  @Test
  void testGetLastFour_NullOrEmptyCardNumber() {
    assertThrows(IllegalArgumentException.class, () -> getLastFour(""));
    assertThrows(IllegalArgumentException.class, () -> getLastFour(null));
  }

  @Test
  void testPassesLuhnCheck_ValidCardNumber() {
    assertTrue(passesLuhnCheck(AUTHORIZED_CARD_NUMBER));
  }

  @Test
  void testPassesLuhnCheck_InvalidCardNumber() {
    assertFalse(passesLuhnCheck(CARD_NUMBER_FAILS_LUHN_CHECK));
  }

  @Test
  void testPassesLuhnCheck_EmptyCardNumber() {
    assertFalse(passesLuhnCheck(""));
  }

  @Test
  void testIsNotExpired_ExpiryDateInFuture() {
    assertTrue(isNotExpired(12, 2025));
  }

  @Test
  void testIsNotExpired_ExpiryDateInCurrentMonthAndYear() {
    assertTrue(isNotExpired(1, 2025));
  }

  @Test
  void testIsNotExpired_ExpiryDateInPastYear() {
    assertFalse(isNotExpired(12, 2024));
  }

}
