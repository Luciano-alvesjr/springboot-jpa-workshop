package com.luciano.workshop.dto;

import com.luciano.workshop.entities.Product;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

public record ProductResponse(Long id, String name, String description, BigDecimal price,
                              String imgUrl, List<CategoryResponse> categories) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(),
            product.getPrice(), product.getImgUrl(), product.getCategories().stream()
                .map(CategoryResponse::from).sorted(Comparator.comparing(CategoryResponse::id)).toList());
    }
}
