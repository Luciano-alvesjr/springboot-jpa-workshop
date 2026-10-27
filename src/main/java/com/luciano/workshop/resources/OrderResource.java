package com.luciano.workshop.resources;

import com.luciano.workshop.dto.*;
import com.luciano.workshop.services.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderResource {
    private final OrderService service;
    public OrderResource(OrderService service) { this.service = service; }
    @GetMapping
    public ResponseEntity<List<OrderResponse>> findAll() { return ResponseEntity.ok(service.findAll()); }
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
}
