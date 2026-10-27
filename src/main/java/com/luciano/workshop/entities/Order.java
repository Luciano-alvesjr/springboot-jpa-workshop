package com.luciano.workshop.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tb_order")
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private java.time.Instant moment;
    @Convert(converter = com.luciano.workshop.entities.enums.OrderStatusConverter.class)
    @Column(nullable = false)
    private com.luciano.workshop.entities.enums.OrderStatus orderStatus;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id")
    private User client;
    @OneToMany(mappedBy = "id.order")
    private java.util.Set<OrderItem> items = new java.util.LinkedHashSet<>();
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    public Order() {}

    public Order(java.time.Instant moment, com.luciano.workshop.entities.enums.OrderStatus orderStatus, User client) {
        this.moment = moment;
        this.orderStatus = orderStatus;
        this.client = client;
    }

    public Long getId() { return id; }

    public java.time.Instant getMoment() { return moment; }
    public com.luciano.workshop.entities.enums.OrderStatus getOrderStatus() { return orderStatus; }
    public User getClient() { return client; }
    public java.util.Set<OrderItem> getItems() { return items; }
    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) {
        this.payment = payment;
        if (payment != null) payment.setOrder(this);
    }
    public java.math.BigDecimal getTotal() {
        return items.stream().map(OrderItem::getSubTotal)
            .reduce(new java.math.BigDecimal("0.00"), java.math.BigDecimal::add);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Order entity)) return false;
        return getId() != null && getId().equals(entity.getId());
    }

    @Override
    public int hashCode() { return Order.class.hashCode(); }
}
