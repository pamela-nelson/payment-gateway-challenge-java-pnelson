package com.checkout.payment.gateway.controller.unit;


import static com.checkout.payment.gateway.controller.utils.TestUtils.createAuthorisedPaymentResponse;
import static com.checkout.payment.gateway.controller.utils.TestUtils.createAuthorisedPostPaymentRequest;
import static com.checkout.payment.gateway.controller.utils.TestUtils.createDeclinedPostPaymentRequest;
import static com.checkout.payment.gateway.enums.PaymentStatus.AUTHORIZED;
import static com.checkout.payment.gateway.enums.PaymentStatus.DECLINED;
import static com.checkout.payment.gateway.util.CardUtils.getLastFour;
import static java.util.UUID.randomUUID;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.repository.PaymentsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("unit")
@Import(TestApplicationConfiguration.class)
class PaymentGatewayControllerTest {

  @Autowired
  private MockMvc mvc;
  @Autowired
  PaymentsRepository paymentsRepository;

  private static final String PAYMENTS_PATH = "/v1/payments";
  private static final String PAYMENTS_PATH_ = PAYMENTS_PATH + "/";

  // Get Payment

  @Test
  void whenPaymentWithIdExistThenCorrectPaymentIsReturned() throws Exception {
    var payment = createAuthorisedPaymentResponse();
    paymentsRepository.add(payment);

    mvc.perform(MockMvcRequestBuilders.get(PAYMENTS_PATH_ + payment.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(payment.getStatus().getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(payment.getCardNumberLastFour()))
        .andExpect(jsonPath("$.expiryMonth").value(payment.getExpiryMonth()))
        .andExpect(jsonPath("$.expiryYear").value(payment.getExpiryYear()))
        .andExpect(jsonPath("$.currency").value(payment.getCurrency()))
        .andExpect(jsonPath("$.amount").value(payment.getAmount()));
  }

  @Test
  void whenPaymentWithIdDoesNotExistThen404IsReturned() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get(PAYMENTS_PATH_ + randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Entity not found"));
  }

  @Test
  void whenPaymentIdIsNotAnUUIDThen400IsReturned() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get(PAYMENTS_PATH_ + 1))
        .andExpect(status().is4xxClientError());
  }

  // Post Payment

  @Test
  void whenPaymentRequestIsAuthorizedThenCorrectPaymentIsReturned() throws Exception {
    var paymentRequest = createAuthorisedPostPaymentRequest();
    var objectMapper = new ObjectMapper();
    var jsonRequest = objectMapper.writeValueAsString(paymentRequest);
    mvc.perform(MockMvcRequestBuilders.post(PAYMENTS_PATH)
        .contentType("application/json")
        .content(jsonRequest))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isString())
        .andExpect(jsonPath("$.status").value(AUTHORIZED.getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(getLastFour(paymentRequest.getCardNumber())))
        .andExpect(jsonPath("$.expiryMonth").value(paymentRequest.getExpiryMonth()))
        .andExpect(jsonPath("$.expiryYear").value(paymentRequest.getExpiryYear()))
        .andExpect(jsonPath("$.currency").value(paymentRequest.getCurrency()))
        .andExpect(jsonPath("$.amount").value(paymentRequest.getAmount()));
  }

  @Test
  void whenPaymentRequestIsRejectedThenCorrectPaymentIsReturned() throws Exception {
    var paymentRequest = createDeclinedPostPaymentRequest();
    var objectMapper = new ObjectMapper();
    var jsonRequest = objectMapper.writeValueAsString(paymentRequest);
    mvc.perform(MockMvcRequestBuilders.post(PAYMENTS_PATH)
            .contentType("application/json")
            .content(jsonRequest))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isString())
        .andExpect(jsonPath("$.status").value(DECLINED.getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(getLastFour(paymentRequest.getCardNumber())))
        .andExpect(jsonPath("$.expiryMonth").value(paymentRequest.getExpiryMonth()))
        .andExpect(jsonPath("$.expiryYear").value(paymentRequest.getExpiryYear()))
        .andExpect(jsonPath("$.currency").value(paymentRequest.getCurrency()))
        .andExpect(jsonPath("$.amount").value(paymentRequest.getAmount()));
  }

  @Test
  void whenPaymentRequestIsRejectedThen422IsReturned() throws Exception {
    var paymentRequest = createAuthorisedPostPaymentRequest();
    paymentRequest.setCardNumber("4701634248325060");
    var objectMapper = new ObjectMapper();
    var jsonRequest = objectMapper.writeValueAsString(paymentRequest);
    mvc.perform(MockMvcRequestBuilders.post(PAYMENTS_PATH)
            .contentType("application/json")
            .content(jsonRequest))
        .andExpect(status().isUnprocessableEntity());
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "44750", //too short
      "4497210378936459", //fails luhn check
      "44579631935861028467", //too long
      "123456789123456P" //contains letter
     })
  void whenCardNumberIsInvalidThen400IsReturned(String cardNumber) throws Exception {
    var paymentRequest = createAuthorisedPostPaymentRequest();
    paymentRequest.setCardNumber(cardNumber);
    var objectMapper = new ObjectMapper();
    var jsonRequest = objectMapper.writeValueAsString(paymentRequest);
    mvc.perform(MockMvcRequestBuilders.post(PAYMENTS_PATH)
            .contentType("application/json")
            .content(jsonRequest))
        .andExpect(status().is4xxClientError());
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 14})
  void whenExpiryMonthIsInvalidThen400IsReturned(int expiryMonth) throws Exception {
    var paymentRequest = createAuthorisedPostPaymentRequest();
    paymentRequest.setExpiryMonth(expiryMonth);
    var objectMapper = new ObjectMapper();
    var jsonRequest = objectMapper.writeValueAsString(paymentRequest);
    mvc.perform(MockMvcRequestBuilders.post(PAYMENTS_PATH)
            .contentType("application/json")
            .content(jsonRequest))
        .andExpect(status().is4xxClientError());
  }

  @ParameterizedTest
  @ValueSource(ints = {2012, 2024})
  void whenExpiryYearIsInvalidThen400IsReturned(int expiryYear) throws Exception {
    var paymentRequest = createAuthorisedPostPaymentRequest();
    paymentRequest.setExpiryMonth(expiryYear);
    var objectMapper = new ObjectMapper();
    var jsonRequest = objectMapper.writeValueAsString(paymentRequest);
    mvc.perform(MockMvcRequestBuilders.post(PAYMENTS_PATH)
            .contentType("application/json")
            .content(jsonRequest))
        .andExpect(status().is4xxClientError());
  }

  @ParameterizedTest
  @CsvSource({
      "1, 2025",
      "6, 2026",
      "10, 2027",
  })
  void whenExpiryYearAndMonthInFutureThenCorrectPaymentIsReturned(int expiryMonth, int expiryYear) throws Exception {
    var paymentRequest = createAuthorisedPostPaymentRequest();
    paymentRequest.setExpiryMonth(expiryMonth);
    paymentRequest.setExpiryYear(expiryYear);
    var objectMapper = new ObjectMapper();
    var jsonRequest = objectMapper.writeValueAsString(paymentRequest);
    mvc.perform(MockMvcRequestBuilders.post(PAYMENTS_PATH)
            .contentType("application/json")
            .content(jsonRequest))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @ValueSource(strings = {"GBP", "USD", "EUR"})
  void whenCurrencyIsSupportedThenCorrectPaymentIsReturned(String currency) throws Exception {
    var paymentRequest = createAuthorisedPostPaymentRequest();
    paymentRequest.setCurrency(currency);
    var objectMapper = new ObjectMapper();
    var jsonRequest = objectMapper.writeValueAsString(paymentRequest);
    mvc.perform(MockMvcRequestBuilders.post(PAYMENTS_PATH)
            .contentType("application/json")
            .content(jsonRequest))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @ValueSource(strings = {"INR", "NZD", "CAD", "DOLLARS"})
  void whenCurrencyIsNotSupportedThen400IsReturned(String currency) throws Exception {
    var paymentRequest = createAuthorisedPostPaymentRequest();
    paymentRequest.setCurrency(currency);
    var objectMapper = new ObjectMapper();
    var jsonRequest = objectMapper.writeValueAsString(paymentRequest);
    mvc.perform(MockMvcRequestBuilders.post(PAYMENTS_PATH)
            .contentType("application/json")
            .content(jsonRequest))
        .andExpect(status().is4xxClientError());
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, 0, -5})
  void whenAmountIsInvalidThen400IsReturned(int amount) throws Exception {
    var paymentRequest = createAuthorisedPostPaymentRequest();
    paymentRequest.setAmount(amount);
    var objectMapper = new ObjectMapper();
    var jsonRequest = objectMapper.writeValueAsString(paymentRequest);
    mvc.perform(MockMvcRequestBuilders.post(PAYMENTS_PATH)
            .contentType("application/json")
            .content(jsonRequest))
        .andExpect(status().is4xxClientError());
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "12", //too short
      "12345", //too long
      "123P" //contains letter
  })
  void whenCvvIsInvalidThen400IsReturned(String cvv) throws Exception {
    var paymentRequest = createAuthorisedPostPaymentRequest();
    paymentRequest.setCvv(cvv);
    var objectMapper = new ObjectMapper();
    var jsonRequest = objectMapper.writeValueAsString(paymentRequest);
    mvc.perform(MockMvcRequestBuilders.post(PAYMENTS_PATH)
            .contentType("application/json")
            .content(jsonRequest))
        .andExpect(status().is4xxClientError());
  }

  @ParameterizedTest
  @ValueSource(strings = {"123", "1234"})
  void whenCvvIsValidThenCorrectPaymentIsReturned(String cvv) throws Exception {
    var paymentRequest = createAuthorisedPostPaymentRequest();
    paymentRequest.setCvv(cvv);
    var objectMapper = new ObjectMapper();
    var jsonRequest = objectMapper.writeValueAsString(paymentRequest);
    mvc.perform(MockMvcRequestBuilders.post(PAYMENTS_PATH)
            .contentType("application/json")
            .content(jsonRequest))
        .andExpect(status().isOk());
  }
}
