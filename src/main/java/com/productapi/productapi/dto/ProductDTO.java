package com.productapi.productapi.dto;

import com.productapi.productapi.model.Product;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductDTO {

    private Long productId;
    private BigDecimal price;
    private Integer quantity;
    private String name;

    public ProductDTO(Product product) {
        this.name = product.getName();
        this.productId = product.getId();
        this.price = product.getPrice();
        this.quantity = product.getStock();
    }
}