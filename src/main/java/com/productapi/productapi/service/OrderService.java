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
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        Set<Long> productIds = orderRequest.getItems().stream()
                .map(OrderItemRequestDTO::getProductId)
                .collect(Collectors.toSet());

        List<Product> foundProducts = productRepository.findAllById(productIds);
        Map<Long, Product> productMap = foundProducts.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        List<UnavailableProductDTO> unavailableProducts = new ArrayList<>();
        for (OrderItemRequestDTO itemDto : orderRequest.getItems()) {
            Product product = productMap.get(itemDto.getProductId());

            if (product == null) {
                throw new ProductNotFoundException("Produto com ID " + itemDto.getProductId() + " não encontrado.");
            }
            if (product.getStock() < itemDto.getQuantity()) {
                unavailableProducts.add(new UnavailableProductDTO(product.getId(), product.getStock()));
            }
        }

        if (!unavailableProducts.isEmpty()) {
            throw new InsufficientStockException("Estoque insuficiente para um ou mais produtos.", unavailableProducts);
        }

        Order newOrder = new Order();
        newOrder.setCreatedAt(LocalDateTime.now());
        BigDecimal totalOrderPrice = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequestDTO itemDto : orderRequest.getItems()) {
            Product product = productMap.get(itemDto.getProductId());
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(newOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setLineTotal(product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
            orderItems.add(orderItem);

            product.setStock(product.getStock() - itemDto.getQuantity());
            totalOrderPrice = totalOrderPrice.add(orderItem.getLineTotal());
        }
        newOrder.setItems(orderItems);
        newOrder.setTotal(totalOrderPrice);

        return orderRepository.save(newOrder);
    }
}
