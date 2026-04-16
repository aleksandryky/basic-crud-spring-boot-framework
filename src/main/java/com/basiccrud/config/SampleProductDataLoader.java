package com.basiccrud.config;

import com.basiccrud.model.Product;
import com.basiccrud.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class SampleProductDataLoader {

    @Bean
    CommandLineRunner loadSampleProducts(ProductRepository productRepository) {
        return args -> {
            if (productRepository.count() > 0) {
                return;
            }
            List<Product> samples = List.of(
                    product("Sample Notebook", "A5 lined, 80 pages", new BigDecimal("12.99")),
                    product("Travel Mug", "Insulated stainless 16 oz", new BigDecimal("24.50")),
                    product("USB-C Cable", "1 m braided", new BigDecimal("8.00")));
            productRepository.saveAll(samples);
        };
    }

    private static Product product(String name, String description, BigDecimal price) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);
        return p;
    }
}
