package com.productapi.productapi.controller;

import com.productapi.productapi.dto.OrderRequestDTO;
import com.productapi.productapi.dto.OrderResponseDTO;
import com.productapi.productapi.model.Order;
import com.productapi.productapi.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> placeOrder(@Valid @RequestBody OrderRequestDTO orderRequest) {
        Order createdOrder = orderService.placeOrder(orderRequest);
        OrderResponseDTO response = new OrderResponseDTO(createdOrder);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
