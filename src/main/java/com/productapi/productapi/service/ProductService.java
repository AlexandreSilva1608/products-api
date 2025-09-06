package com.productapi.productapi.service;

import com.productapi.productapi.dto.ProductDTO;
import com.productapi.productapi.model.Product;
import com.productapi.productapi.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ProductService implements ProductServiceInterface{

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @Override
    public Page<ProductDTO> listAllProductsPaginated(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));

        Page<Product> productPage;
        if (name == null || name.trim().isEmpty()) {
            productPage = productRepository.findAll(pageable);
        } else {
            productPage = productRepository.findByNameContainingIgnoreCase(name, pageable);
        }

        return productPage.map(ProductDTO::new);
    }
}