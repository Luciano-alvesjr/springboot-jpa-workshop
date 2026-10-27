package com.luciano.workshop.resources;

import com.luciano.workshop.dto.*;
import com.luciano.workshop.services.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryResource {
    private final CategoryService service;
    public CategoryResource(CategoryService service) { this.service = service; }
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> findAll() { return ResponseEntity.ok(service.findAll()); }
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
}
