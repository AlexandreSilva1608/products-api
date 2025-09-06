package com.productapi.productapi.service;

import com.productapi.productapi.dto.OrderRequestDTO;
import com.productapi.productapi.model.Order;

public interface OrderServiceInterface {

    Order placeOrder(OrderRequestDTO orderRequest);
}
