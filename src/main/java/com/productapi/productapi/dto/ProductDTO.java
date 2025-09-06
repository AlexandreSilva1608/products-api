package com.productapi.productapi.dto;

import com.productapi.productapi.model.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {

    private Long productId;
    private Integer quantity;
    private String name;

    public ProductDTO(Product product) {
        this.name = product.getName();
        this.productId = product.getId();
        this.quantity = product.getStock();
    }
}