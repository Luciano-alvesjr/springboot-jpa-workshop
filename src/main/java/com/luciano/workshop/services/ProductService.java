package com.luciano.workshop.services;

import com.luciano.workshop.dto.ProductResponse;
import com.luciano.workshop.repositories.ProductRepository;
import com.luciano.workshop.services.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository repository;
    public ProductService(ProductRepository repository) { this.repository = repository; }
    public List<ProductResponse> findAll() {
        return repository.findAll(Sort.by("id")).stream().map(ProductResponse::from).toList();
    }
    public ProductResponse findById(Long id) {
        return ProductResponse.from(repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", id)));
    }
}
