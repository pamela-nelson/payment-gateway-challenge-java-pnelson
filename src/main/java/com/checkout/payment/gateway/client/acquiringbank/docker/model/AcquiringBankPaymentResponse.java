package com.checkout.payment.gateway.client.acquiringbank.docker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class AcquiringBankPaymentResponse {

  @JsonProperty("authorized")
  private boolean authorized;

  @JsonProperty("authorization_code")
  private UUID authorizationCode;

  public AcquiringBankPaymentResponse() {}

  public AcquiringBankPaymentResponse(boolean authorized, UUID authorizationCode) {
    this.authorized = authorized;
    this.authorizationCode = authorizationCode;
  }

  public boolean getAuthorized() {
    return authorized;
  }

  public void setAuthorized(boolean authorized) {
    this.authorized = authorized;
  }

  public UUID getAuthorizationCode() {
    return authorizationCode;
  }

  public void setAuthorizationCode(UUID authorizationCode) {
    this.authorizationCode = authorizationCode;
  }
}
