package com.productapi.productapi.service;

import com.productapi.productapi.dto.ProductDTO;
import org.springframework.data.domain.Page;

public interface ProductServiceInterface {

    Page<ProductDTO> listAllProductsPaginated(String name, int page, int size);
}
