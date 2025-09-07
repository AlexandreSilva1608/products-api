package com.productapi.productapi.service;

import com.productapi.productapi.dto.OrderItemRequestDTO;
import com.productapi.productapi.dto.OrderRequestDTO;
import com.productapi.productapi.exception.InsufficientStockException;
import com.productapi.productapi.exception.ProductNotFoundException;
import com.productapi.productapi.model.Order;
import com.productapi.productapi.model.Product;
import com.productapi.productapi.repository.OrderRepository;
import com.productapi.productapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product("Café 500g", new BigDecimal("18.90"), 5);
        product1.setId(1L);

        product2 = new Product("Garrafa Térmica 1L", new BigDecimal("79.90"), 2);
        product2.setId(2L);
    }

    @Test
    @DisplayName("Deve criar um pedido com sucesso quando o estoque for suficiente")
    void shouldPlaceOrderSuccessfullyWhenStockIsSufficient() {
        OrderItemRequestDTO item1Dto = new OrderItemRequestDTO();
        item1Dto.setProductId(1L);
        item1Dto.setQuantity(2);

        OrderRequestDTO orderRequest = new OrderRequestDTO();
        orderRequest.setItems(List.of(item1Dto));

        when(productRepository.findAllById(Set.of(1L))).thenReturn(List.of(product1));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order createdOrder = orderService.placeOrder(orderRequest);

        assertNotNull(createdOrder);
        assertEquals(1, createdOrder.getItems().size());
        assertEquals(0, new BigDecimal("37.80").compareTo(createdOrder.getTotal()));
        assertEquals(8, product1.getStock());

        verify(productRepository, times(1)).findAllById(any());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Deve lançar InsufficientStockException quando o estoque for insuficiente")
    void shouldThrowInsufficientStockExceptionWhenStockIsUnavailable() {
        OrderItemRequestDTO itemDto = new OrderItemRequestDTO();
        itemDto.setProductId(2L);
        itemDto.setQuantity(10);

        OrderRequestDTO orderRequest = new OrderRequestDTO();
        orderRequest.setItems(List.of(itemDto));

        when(productRepository.findAllById(Set.of(2L))).thenReturn(List.of(product2));

        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> orderService.placeOrder(orderRequest)
        );

        assertEquals("Estoque insuficiente para um ou mais produtos.", exception.getMessage());
        assertEquals(1, exception.getUnavailableProducts().size());
        assertEquals(2L, exception.getUnavailableProducts().get(0).getProductId());

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Deve lançar ProductNotFoundException quando um produto não existir")
    void shouldThrowProductNotFoundExceptionWhenProductDoesNotExist() {
        long nonExistentProductId = 99L;
        OrderItemRequestDTO itemDto = new OrderItemRequestDTO();
        itemDto.setProductId(nonExistentProductId);
        itemDto.setQuantity(1);

        OrderRequestDTO orderRequest = new OrderRequestDTO();
        orderRequest.setItems(List.of(itemDto));

        when(productRepository.findAllById(Set.of(nonExistentProductId))).thenReturn(Collections.emptyList());

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> orderService.placeOrder(orderRequest)
        );

        assertEquals("Produto com ID 99 não encontrado.", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Deve falhar ao tentar finalizar a compra quando um dos produtos tiver estoque insuficiente")
    void shouldFailOrderWhenOneProductHasInsufficientStock() {
        OrderItemRequestDTO garrafaDto = new OrderItemRequestDTO();
        garrafaDto.setProductId(2L);
        garrafaDto.setQuantity(3);

        OrderItemRequestDTO cafeDto = new OrderItemRequestDTO();
        cafeDto.setProductId(1L);
        cafeDto.setQuantity(2);

        OrderRequestDTO orderRequest = new OrderRequestDTO();
        orderRequest.setItems(List.of(garrafaDto, cafeDto));

        when(productRepository.findAllById(Set.of(2L, 1L))).thenReturn(List.of(product2, product1));

        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> orderService.placeOrder(orderRequest)
        );

        assertEquals("Estoque insuficiente para um ou mais produtos.", exception.getMessage());
        assertEquals(1, exception.getUnavailableProducts().size());
        assertEquals(2L, exception.getUnavailableProducts().get(0).getProductId());

        verify(orderRepository, never()).save(any(Order.class));
    }

}

