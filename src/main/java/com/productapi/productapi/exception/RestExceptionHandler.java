package com.productapi.productapi.exception;

import com.productapi.productapi.dto.UnavailableProductDTO;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(InsufficientStockException.class)
  public ResponseEntity<List<UnavailableProductDTO>> handleInsufficientStockException(InsufficientStockException ex) {
    return new ResponseEntity<>(ex.getUnavailableProducts(), HttpStatus.CONFLICT);
  }

  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<Object> handleProductNotFoundException(ProductNotFoundException ex) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage());
    return new ResponseEntity<>(apiError, apiError.getStatus());
  }

  @Getter
  private static class ApiError {
    private HttpStatus status;
    private String message;

    public ApiError(HttpStatus status, String message) {
      this.status = status;
      this.message = message;
    }

  }
}
