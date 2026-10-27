package com.luciano.workshop.entities.pk;

import com.luciano.workshop.entities.Order;
import com.luciano.workshop.entities.Product;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OrderItemPK implements Serializable {
    private static final long serialVersionUID = 1L;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id")
    private Order order;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    public OrderItemPK() {}
    public OrderItemPK(Order order, Product product) { this.order = order; this.product = product; }
    public Order getOrder() { return order; }
    public Product getProduct() { return product; }
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof OrderItemPK key)) return false;
        return Objects.equals(order, key.order) && Objects.equals(product, key.product);
    }
    @Override
    public int hashCode() { return Objects.hash(order, product); }
}
