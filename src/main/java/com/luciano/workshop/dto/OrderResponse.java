package com.luciano.workshop.dto;

import com.luciano.workshop.entities.Order;
import com.luciano.workshop.entities.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

public record OrderResponse(Long id, Instant moment, OrderStatus orderStatus, UserResponse client,
                            List<ItemResponse> items, PaymentResponse payment, BigDecimal total) {
    public record ItemResponse(Long productId, String productName, Integer quantity,
                               BigDecimal price, BigDecimal subTotal) {}
    public record PaymentResponse(Long id, Instant moment) {}

    public static OrderResponse from(Order order) {
        var items = order.getItems().stream().map(item -> new ItemResponse(item.getProduct().getId(),
            item.getProduct().getName(), item.getQuantity(), item.getPrice(), item.getSubTotal()))
            .sorted(Comparator.comparing(ItemResponse::productId)).toList();
        var payment = order.getPayment() == null ? null
            : new PaymentResponse(order.getPayment().getId(), order.getPayment().getMoment());
        return new OrderResponse(order.getId(), order.getMoment(), order.getOrderStatus(),
            UserResponse.from(order.getClient()), items, payment, order.getTotal());
    }
}
