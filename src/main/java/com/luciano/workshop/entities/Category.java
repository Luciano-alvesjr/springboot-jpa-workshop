package com.luciano.workshop.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tb_category")
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;
    @ManyToMany(mappedBy = "categories")
    private java.util.Set<Product> products = new java.util.LinkedHashSet<>();

    public Category() {}

    public Category(String name) {
        this.name = name;
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public java.util.Set<Product> getProducts() { return products; }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Category entity)) return false;
        return getId() != null && getId().equals(entity.getId());
    }

    @Override
    public int hashCode() { return Category.class.hashCode(); }
}
