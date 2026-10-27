package com.luciano.workshop.entities;

import com.luciano.workshop.entities.pk.OrderItemPK;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "tb_order_item")
public class OrderItem implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    private OrderItemPK id = new OrderItemPK();
    @Column(nullable = false)
    private Integer quantity;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    public OrderItem() {}
    public OrderItem(Order order, Product product, Integer quantity, BigDecimal price) {
        if (quantity == null || quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        if (price == null || price.signum() < 0) throw new IllegalArgumentException("Price must not be negative");
        this.id = new OrderItemPK(order, product);
        this.quantity = quantity;
        this.price = price;
    }
    public Order getOrder() { return id.getOrder(); }
    public Product getProduct() { return id.getProduct(); }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getSubTotal() { return price.multiply(BigDecimal.valueOf(quantity)); }
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof OrderItem item)) return false;
        return Objects.equals(id, item.id);
    }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
