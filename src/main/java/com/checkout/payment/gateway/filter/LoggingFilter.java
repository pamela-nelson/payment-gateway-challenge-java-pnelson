package com.checkout.payment.gateway.filter;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * Logging class to log rest requests and responses to the service.
 * Includes a correlationId that is tracked throughout the journey
 * Masks Sensitive Key fields in logs
 */
@Component
public class LoggingFilter implements Filter {
  private static final Logger LOG = LoggerFactory.getLogger(LoggingFilter.class);

  private static final Set<String> SENSITIVE_KEYS = Set.of("cardNumber", "cvv");
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final String CORRELATION_ID = "correlationId";

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    var httpRequest = (HttpServletRequest) request;
    var httpResponse = (HttpServletResponse) response;

    var correlationId = httpRequest.getHeader(CORRELATION_ID);
    if (correlationId == null) {
      correlationId = UUID.randomUUID().toString();
    }

    MDC.put(CORRELATION_ID, correlationId);

    try {
      var wrappedRequest = new CachedBodyHttpServletRequest(httpRequest);
      var fullUrl = httpRequest.getScheme() + "://" + httpRequest.getHeader("Host") + httpRequest.getRequestURI();
      var maskedPayload = maskSensitiveData(wrappedRequest.getBody());

      LOG.info("REQUEST: {} {} {}",
          wrappedRequest.getMethod(),
          fullUrl,
          maskedPayload);

      chain.doFilter(wrappedRequest, response);

      LOG.info("RESPONSE: {} - {} {}",
          httpResponse.getStatus(),
          httpRequest.getMethod(),
          fullUrl);

    } finally {
      MDC.remove(CORRELATION_ID);
    }
  }

  private String maskSensitiveData(String payload) {
    try {
      var jsonNode = objectMapper.readTree(payload);
      maskSensitiveFields(jsonNode);
      return objectMapper.writeValueAsString(jsonNode);
    } catch (Exception e) {
      LOG.warn("Failed to parse or mask sensitive data in payload", e);
      return payload;
    }
  }

  private void maskSensitiveFields(JsonNode node) {
    if (node.isObject()) {
      var fieldNames = node.fieldNames();
      while (fieldNames.hasNext()) {
        var fieldName = fieldNames.next();
        if (SENSITIVE_KEYS.contains(fieldName) && node.get(fieldName).isTextual()) {
          ((ObjectNode) node).put(fieldName, "*****");
        } else {
          maskSensitiveFields(node.get(fieldName));
        }
      }
    } else if (node.isArray()) {
      for (var arrayItem : node) {
        maskSensitiveFields(arrayItem);
      }
    }
  }

  private static class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
    private final byte[] cachedBody;

    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
      super(request);
      try (var reader = new BufferedReader(new InputStreamReader(request.getInputStream(), UTF_8))) {
        var stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          stringBuilder.append(line);
        }
        cachedBody = stringBuilder.toString().getBytes(UTF_8);
      }
    }

    @Override
    public ServletInputStream getInputStream() {
      return new CachedBodyInputStream(cachedBody);
    }

    public String getBody() {
      return new String(cachedBody, UTF_8);
    }

    private static class CachedBodyInputStream extends ServletInputStream {
      private final byte[] body;
      private int index;

      public CachedBodyInputStream(byte[] body) {
        this.body = body;
      }

      @Override
      public boolean isFinished() {
        return index >= body.length;
      }

      @Override
      public boolean isReady() {
        return true;
      }

      @Override
      public void setReadListener(ReadListener readListener) {
      }

      @Override
      public int read() {
        return (isFinished() ? -1 : body[index++]) & 0xFF;
      }
    }
  }
}
