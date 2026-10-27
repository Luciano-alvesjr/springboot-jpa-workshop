package com.luciano.workshop.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tb_product")
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;
    @Column(nullable = false, length = 2000)
    private String description;
    @Column(nullable = false, precision = 19, scale = 2)
    private java.math.BigDecimal price;
    @Column(length = 1000)
    private String imgUrl;
    @ManyToMany
    @JoinTable(name = "tb_product_category",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id"))
    private java.util.Set<Category> categories = new java.util.LinkedHashSet<>();
    @OneToMany(mappedBy = "id.product")
    private java.util.Set<OrderItem> items = new java.util.LinkedHashSet<>();

    public Product() {}

    public Product(String name, String description, java.math.BigDecimal price, String imgUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imgUrl = imgUrl;
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public java.math.BigDecimal getPrice() { return price; }
    public void setPrice(java.math.BigDecimal price) { this.price = price; }
    public String getImgUrl() { return imgUrl; }
    public java.util.Set<Category> getCategories() { return categories; }
    public java.util.Set<OrderItem> getItems() { return items; }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Product entity)) return false;
        return getId() != null && getId().equals(entity.getId());
    }

    @Override
    public int hashCode() { return Product.class.hashCode(); }
}
