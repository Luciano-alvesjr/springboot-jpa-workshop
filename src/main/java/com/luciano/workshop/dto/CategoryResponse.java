package com.luciano.workshop.dto;

import com.luciano.workshop.entities.Category;

public record CategoryResponse(Long id, String name) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }
}
