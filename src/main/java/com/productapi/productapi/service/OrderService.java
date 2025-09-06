package com.productapi.productapi.service;

import com.productapi.productapi.dto.OrderItemRequestDTO;
import com.productapi.productapi.dto.OrderRequestDTO;
import com.productapi.productapi.dto.UnavailableProductDTO;
import com.productapi.productapi.exception.InsufficientStockException;
import com.productapi.productapi.exception.ProductNotFoundException;
import com.productapi.productapi.model.Order;
import com.productapi.productapi.model.OrderItem;
import com.productapi.productapi.model.Product;
import com.productapi.productapi.repository.OrderRepository;
import com.productapi.productapi.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService implements OrderServiceInterface {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order placeOrder(OrderRequestDTO orderRequest) {
        List<UnavailableProductDTO> unavailableProducts = new ArrayList<>();
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalOrderPrice = BigDecimal.ZERO;

        for (OrderItemRequestDTO itemDto : orderRequest.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException("Produto com ID " + itemDto.getProductId() + " não encontrado."));

            if (product.getStock() < itemDto.getQuantity()) {
                unavailableProducts.add(new UnavailableProductDTO(product.getId(), product.getStock()));
            }
        }

        if (!unavailableProducts.isEmpty()) {
            throw new InsufficientStockException("Estoque insuficiente para um ou mais produtos.", unavailableProducts);
        }

        Order newOrder = new Order();
        newOrder.setCreatedAt(LocalDateTime.now());

        for (OrderItemRequestDTO itemDto : orderRequest.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId()).get();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(newOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setLineTotal(product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
            orderItems.add(orderItem);

            product.setStock(product.getStock() - itemDto.getQuantity());
            productRepository.save(product);

            totalOrderPrice = totalOrderPrice.add(orderItem.getLineTotal());
        }

        newOrder.setItems(orderItems);
        newOrder.setTotal(totalOrderPrice);

        return orderRepository.save(newOrder);
    }
}
