package com.productapi.productapi.exception;

import com.productapi.productapi.dto.UnavailableProductDTO;
import lombok.Getter;

import java.util.List;

@Getter
public class InsufficientStockException extends RuntimeException {

    private final List<UnavailableProductDTO> unavailableProducts;

    public InsufficientStockException(String message, List<UnavailableProductDTO> unavailableProducts) {
        super(message);
        this.unavailableProducts = unavailableProducts;
    }

}
