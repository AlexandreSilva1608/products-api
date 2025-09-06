package com.productapi.productapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.productapi.productapi.dto.OrderItemRequestDTO;
import com.productapi.productapi.dto.OrderRequestDTO;
import com.productapi.productapi.dto.UnavailableProductDTO;
import com.productapi.productapi.exception.InsufficientStockException;
import com.productapi.productapi.model.Order;
import com.productapi.productapi.service.OrderService;
import com.productapi.productapi.service.OrderServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public OrderService orderServiceInterface() {
            return Mockito.mock(OrderService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderServiceInterface;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        Mockito.reset(orderServiceInterface);
    }

    @Test
    @DisplayName("POST /orders - Deve criar um pedido com sucesso e retornar status 201")
    void shouldCreateOrderSuccessfully() throws Exception {

        OrderItemRequestDTO itemDto = new OrderItemRequestDTO();
        itemDto.setProductId(1L);
        itemDto.setQuantity(1);

        OrderRequestDTO requestDTO = new OrderRequestDTO();
        requestDTO.setItems(List.of(itemDto));

        Order createdOrder = new Order();
        createdOrder.setId(1L);
        createdOrder.setTotal(new BigDecimal("18.90"));

        when(orderServiceInterface.placeOrder(any(OrderRequestDTO.class))).thenReturn(createdOrder);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.total").value(18.90));
    }

    @Test
    @DisplayName("POST /orders - Deve retornar erro 409 quando o estoque for insuficiente")
    void shouldReturn409WhenStockIsInsufficient() throws Exception {
        OrderItemRequestDTO itemDto = new OrderItemRequestDTO();
        itemDto.setProductId(1L);
        itemDto.setQuantity(10);

        OrderRequestDTO requestDTO = new OrderRequestDTO();
        requestDTO.setItems(List.of(itemDto));

        List<UnavailableProductDTO> unavailableProducts = List.of(new UnavailableProductDTO(1L, 5));
        when(orderServiceInterface.placeOrder(any(OrderRequestDTO.class)))
                .thenThrow(new InsufficientStockException("Estoque insuficiente", unavailableProducts));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict()) // Espera o status 409 Conflict
                .andExpect(jsonPath("$[0].productId").value(1L))
                .andExpect(jsonPath("$[0].available").value(5));
    }

    @Test
    @DisplayName("POST /orders - Deve retornar erro 400 para uma requisição inválida")
    void shouldReturn400ForInvalidRequest() throws Exception {
        OrderRequestDTO invalidRequest = new OrderRequestDTO();
        invalidRequest.setItems(Collections.emptyList());

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}

