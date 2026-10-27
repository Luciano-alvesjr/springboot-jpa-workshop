package com.luciano.workshop.services;

import com.luciano.workshop.dto.CategoryResponse;
import com.luciano.workshop.repositories.CategoryRepository;
import com.luciano.workshop.services.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository repository;
    public CategoryService(CategoryRepository repository) { this.repository = repository; }
    public List<CategoryResponse> findAll() {
        return repository.findAll(Sort.by("id")).stream().map(CategoryResponse::from).toList();
    }
    public CategoryResponse findById(Long id) {
        return CategoryResponse.from(repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category", id)));
    }
}
