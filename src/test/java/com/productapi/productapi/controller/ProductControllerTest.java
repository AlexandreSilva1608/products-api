package com.productapi.productapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.productapi.productapi.dto.ProductDTO;
import com.productapi.productapi.model.Product;
import com.productapi.productapi.service.ProductServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ProductServiceInterface productServiceInterface() {
            return Mockito.mock(ProductServiceInterface.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductServiceInterface productServiceInterface;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        Mockito.reset(productServiceInterface);
    }

    @Test
    @DisplayName("GET /products - Deve retornar lista paginada de produtos com status 200")
    void shouldReturnPaginatedListOfProducts() throws Exception {
        Product product = new Product("Café", new BigDecimal("20.00"), 100);
        product.setId(1L);
        ProductDTO productDTO = new ProductDTO(product);
        Page<ProductDTO> productPage = new PageImpl<>(List.of(productDTO), PageRequest.of(0, 5), 1);

        when(productServiceInterface.listAllProductsPaginated("", 0, 5)).thenReturn(productPage);

        mockMvc.perform(get("/products")
                        .param("search", "")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].productId").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("Café"))
                .andExpect(jsonPath("$.content[0].quantity").value(100))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /products - Deve retornar lista filtrada por nome com status 200")
    void shouldReturnFilteredListOfProducts() throws Exception {
        String searchTerm = "Garrafa";
        Product product = new Product("Garrafa Térmica", new BigDecimal("80.00"), 10);
        product.setId(2L);
        ProductDTO productDTO = new ProductDTO(product);
        Page<ProductDTO> productPage = new PageImpl<>(List.of(productDTO), PageRequest.of(0, 5), 1);

        when(productServiceInterface.listAllProductsPaginated(searchTerm, 0, 5)).thenReturn(productPage);

        mockMvc.perform(get("/products")
                        .param("search", searchTerm)
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].productId").value(2L))
                .andExpect(jsonPath("$.content[0].name").value("Garrafa Térmica"))
                .andExpect(jsonPath("$.numberOfElements").value(1));
    }
}

