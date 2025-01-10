package com.checkout.payment.gateway.repository;

import static org.slf4j.LoggerFactory.getLogger;

import com.checkout.payment.gateway.model.PostPaymentResponse;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentsRepository {
  private static final Logger LOG = getLogger(PaymentsRepository.class);

  private final HashMap<UUID, PostPaymentResponse> payments = new HashMap<>();

  public void add(PostPaymentResponse payment) {
    payments.put(payment.getId(), payment);
    LOG.debug("Payment added to db. paymentId={}", payment.getId());
  }

  public Optional<PostPaymentResponse> get(UUID id) {
    return Optional.ofNullable(payments.get(id));
  }

}
