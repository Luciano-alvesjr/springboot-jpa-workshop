package com.luciano.workshop.entities.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class OrderStatusConverter implements AttributeConverter<OrderStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(OrderStatus status) {
        return status == null ? null : status.getCode();
    }
    @Override
    public OrderStatus convertToEntityAttribute(Integer code) {
        return code == null ? null : OrderStatus.fromCode(code);
    }
}
