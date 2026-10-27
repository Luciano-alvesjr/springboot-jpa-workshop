package com.luciano.workshop.entities.enums;

public enum OrderStatus {
    WAITING_PAYMENT(1), PAID(2), SHIPPED(3), DELIVERED(4), CANCELED(5);

    private final int code;
    OrderStatus(int code) { this.code = code; }
    public int getCode() { return code; }
    public static OrderStatus fromCode(int code) {
        for (OrderStatus status : values()) if (status.code == code) return status;
        throw new IllegalArgumentException("Invalid order status code: " + code);
    }
}
