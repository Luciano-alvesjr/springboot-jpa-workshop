package com.luciano.workshop.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "tb_payment")
public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private Long id;
    @Column(nullable = false)
    private Instant moment;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "order_id")
    private Order order;

    public Payment() {}
    public Payment(Instant moment, Order order) { this.moment = moment; this.order = order; }
    public Long getId() { return id; }
    public Instant getMoment() { return moment; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Payment payment)) return false;
        return getId() != null && getId().equals(payment.getId());
    }
    @Override
    public int hashCode() { return Payment.class.hashCode(); }
}
