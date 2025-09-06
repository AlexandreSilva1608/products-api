package com.productapi.productapi.config;

import com.productapi.productapi.model.Product;
import com.productapi.productapi.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Este componente é executado na inicialização da aplicação
 * para popular o banco de dados com dados iniciais, se necessário.
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);
    private final ProductRepository productRepository;

    public DatabaseInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Verifica se a tabela de produtos já tem dados para evitar duplicatas a cada reinicialização
        if (productRepository.count() > 0) {
            logger.info("O banco de dados de produtos já está populado. Nenhuma ação necessária.");
            return;
        }

        logger.info("Populando o banco de dados com produtos iniciais...");

        List<Product> products = getProducts();

        productRepository.saveAll(products);

        logger.info("Banco de dados populado com {} produtos.", products.size());
    }

    private static List<Product> getProducts() {
        Product product1 = new Product("Café Torrado 500g", new BigDecimal("18.90"), 5);
        Product product2 = new Product("Filtro de Papel nº103", new BigDecimal("7.50"), 10);
        Product product3 = new Product("Garrafa Térmica 1L", new BigDecimal("79.90"), 2);
        Product product4 = new Product("Açúcar Mascavo 1kg", new BigDecimal("16.00"), 0);
        Product product5 = new Product("Caneca Inox 300ml", new BigDecimal("29.00"), 8);

        return Arrays.asList(product1, product2, product3, product4, product5);
    }
}
