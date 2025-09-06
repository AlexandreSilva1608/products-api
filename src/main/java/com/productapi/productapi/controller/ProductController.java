package com.productapi.productapi.controller;

import com.productapi.productapi.dto.ProductDTO;
import com.productapi.productapi.model.Product;
import com.productapi.productapi.service.ProductServiceInterface;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("products")
public class ProductController {

    private final ProductServiceInterface productServiceInterface;

    public ProductController(ProductServiceInterface productServiceInterface) {
        this.productServiceInterface = productServiceInterface;
    }

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(@RequestParam(defaultValue = "") String search, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<ProductDTO> productPage = productServiceInterface.listAllProductsPaginated(search, page, size);
        return ResponseEntity.ok(productPage);
    }

}
