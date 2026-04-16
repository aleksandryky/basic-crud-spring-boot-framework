package com.basiccrud.service;

import com.basiccrud.model.Product;
import com.basiccrud.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional
    public Product create(Product product) {
        product.setId(null);
        return productRepository.save(product);
    }

    @Transactional
    public Optional<Product> update(Long id, Product incoming) {
        return productRepository.findById(id).map(existing -> {
            existing.setName(incoming.getName());
            existing.setDescription(incoming.getDescription());
            existing.setPrice(incoming.getPrice());
            return productRepository.save(existing);
        });
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (!productRepository.existsById(id)) {
            return false;
        }
        productRepository.deleteById(id);
        return true;
    }
}
