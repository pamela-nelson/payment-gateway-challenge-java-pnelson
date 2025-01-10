package com.checkout.payment.gateway.exception;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import com.checkout.payment.gateway.client.acquiringbank.docker.exception.UnsupportedCardException;
import com.checkout.payment.gateway.model.ErrorResponse;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class CommonExceptionHandler {

  private static final Logger LOG = getLogger(CommonExceptionHandler.class);

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFoundException(EntityNotFoundException ex) {
    return new ResponseEntity<>(new ErrorResponse("Entity not found"),
        NOT_FOUND);
  }

  @ExceptionHandler(AcquiringBankClientException.class)
  public ResponseEntity<ErrorResponse> handleAcquiringBankClientException(AcquiringBankClientException ex) {
    return new ResponseEntity<>(new ErrorResponse("Error processing payment. Try again later."),
        BAD_GATEWAY);
  }

  @ExceptionHandler(UnsupportedCardException.class)
  public ResponseEntity<ErrorResponse> handleUnsupportedCardException(UnsupportedCardException ex) {
    return new ResponseEntity<>(new ErrorResponse("status: REJECTED. Card not supported"),
        UNPROCESSABLE_ENTITY);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentException(MethodArgumentTypeMismatchException ex) {
    return new ResponseEntity<>(new ErrorResponse("Invalid argument format"),
        BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
    var errorMessages = ((MethodArgumentNotValidException) ex)
        .getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .toList();

    var errors = new StringBuilder("status: REJECTED\n");
    errorMessages.forEach(message -> errors.append(message).append("\n"));

    var formattedErrors = errors.toString().trim();
    return new ResponseEntity<>(new ErrorResponse(formattedErrors), BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    LOG.error("An unexpected error occurred", ex);
    return new ResponseEntity<>(new ErrorResponse("An unexpected error occurred"),
        INTERNAL_SERVER_ERROR);
  }
}
