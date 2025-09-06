package com.productapi.productapi.service;

import com.productapi.productapi.dto.ProductDTO;
import com.productapi.productapi.model.Product;
import com.productapi.productapi.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("Deve listar produtos paginados quando nenhum termo de busca é fornecido")
    void shouldListPaginatedProductsWhenNoSearchTermIsProvided() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
        Product product = new Product("Café", new BigDecimal("20.00"), 100);
        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);

        when(productRepository.findAll(pageable)).thenReturn(productPage);

        Page<ProductDTO> result = productService.listAllProductsPaginated("", 0, 10);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("Café", result.getContent().get(0).getName());

        verify(productRepository, times(1)).findAll(pageable);
        verify(productRepository, never()).findByNameContainingIgnoreCase(anyString(), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve buscar produtos por nome quando um termo de busca é fornecido")
    void shouldSearchProductsByNameWhenSearchTermIsProvided() {
        String searchTerm = "Café";
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
        Product product = new Product("Café Torrado", new BigDecimal("25.00"), 50);
        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);

        when(productRepository.findByNameContainingIgnoreCase(searchTerm, pageable)).thenReturn(productPage);

        Page<ProductDTO> result = productService.listAllProductsPaginated(searchTerm, 0, 10);

        assertEquals(1, result.getTotalElements());
        assertEquals("Café Torrado", result.getContent().get(0).getName());

        verify(productRepository, never()).findAll(pageable);
        verify(productRepository, times(1)).findByNameContainingIgnoreCase(searchTerm, pageable);
    }
}

