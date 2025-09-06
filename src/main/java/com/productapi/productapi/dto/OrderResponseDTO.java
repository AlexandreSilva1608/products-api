package com.productapi.productapi.dto;

import com.productapi.productapi.model.Order;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class OrderResponseDTO {

    private Long id;
    private LocalDateTime createdAt;
    private BigDecimal total;
    private List<OrderItemResponseDTO> items;

    public OrderResponseDTO(Order order) {
        this.id = order.getId();
        this.createdAt = order.getCreatedAt();
        this.total = order.getTotal();
        this.items = order.getItems().stream()
                .map(OrderItemResponseDTO::new)
                .collect(Collectors.toList());
    }

}
