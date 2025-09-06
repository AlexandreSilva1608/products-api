package com.productapi.productapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnavailableProductDTO {

    private Long productId;
    private Integer available;

    public UnavailableProductDTO(Long productId, Integer available) {
        this.productId = productId;
        this.available = available;
    }

}
