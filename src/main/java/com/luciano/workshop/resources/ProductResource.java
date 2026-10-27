package com.luciano.workshop.resources;

import com.luciano.workshop.dto.*;
import com.luciano.workshop.services.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductResource {
    private final ProductService service;
    public ProductResource(ProductService service) { this.service = service; }
    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAll() { return ResponseEntity.ok(service.findAll()); }
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
}
