package com.luciano.workshop.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tb_user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;
    @Column(nullable = false, unique = true, length = 254)
    private String email;
    @Column(nullable = false, length = 30)
    private String phone;
    @Column(nullable = false, length = 100)
    private String password;
    @OneToMany(mappedBy = "client")
    private java.util.List<Order> orders = new java.util.ArrayList<>();

    public User() {}

    public User(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    @com.fasterxml.jackson.annotation.JsonIgnore
    public String getPassword() { return password; }
    public java.util.List<Order> getOrders() { return orders; }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof User entity)) return false;
        return getId() != null && getId().equals(entity.getId());
    }

    @Override
    public int hashCode() { return User.class.hashCode(); }
}
