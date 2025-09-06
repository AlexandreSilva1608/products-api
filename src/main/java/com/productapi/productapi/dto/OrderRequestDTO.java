package com.productapi.productapi.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequestDTO {

    @NotEmpty(message = "A lista de itens não pode ser vazia.")
    private List<@Valid OrderItemRequestDTO> items;

}
