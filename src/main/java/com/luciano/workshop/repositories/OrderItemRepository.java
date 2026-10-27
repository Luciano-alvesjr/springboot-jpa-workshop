package com.luciano.workshop.repositories;

import com.luciano.workshop.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, com.luciano.workshop.entities.pk.OrderItemPK> {}
