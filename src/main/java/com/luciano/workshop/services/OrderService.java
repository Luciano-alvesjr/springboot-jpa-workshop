package com.luciano.workshop.services;

import com.luciano.workshop.dto.OrderResponse;
import com.luciano.workshop.repositories.OrderRepository;
import com.luciano.workshop.services.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository repository;
    public OrderService(OrderRepository repository) { this.repository = repository; }
    public List<OrderResponse> findAll() {
        return repository.findAll(Sort.by("id")).stream().map(OrderResponse::from).toList();
    }
    public OrderResponse findById(Long id) {
        return OrderResponse.from(repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order", id)));
    }
}
