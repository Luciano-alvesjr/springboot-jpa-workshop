package com.luciano.workshop;

import com.luciano.workshop.entities.*;
import com.luciano.workshop.entities.enums.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import java.math.BigDecimal;
import java.time.Instant;
import static org.assertj.core.api.Assertions.*;

class DomainTests {
    @Test
    void computesExactTotalsWithMultipleItems() {
        Order order = new Order(Instant.now(), OrderStatus.WAITING_PAYMENT, new User());
        Product a = new Product("A", "First", new BigDecimal("0.10"), "");
        Product b = new Product("B", "Second", new BigDecimal("0.20"), "");
        order.getItems().add(new OrderItem(order, a, 3, a.getPrice()));
        order.getItems().add(new OrderItem(order, b, 2, b.getPrice()));
        assertThat(order.getTotal()).isEqualByComparingTo("0.70");
    }

    @Test
    void changingCatalogPriceDoesNotChangeOrderHistory() {
        Order order = new Order();
        Product product = new Product("Book", "A book", new BigDecimal("90.50"), "");
        OrderItem item = new OrderItem(order, product, 2, product.getPrice());
        product.setPrice(new BigDecimal("120.00"));
        assertThat(item.getPrice()).isEqualByComparingTo("90.50");
        assertThat(item.getSubTotal()).isEqualByComparingTo("181.00");
    }

    @Test
    void emptyOrderHasZeroTotal() { assertThat(new Order().getTotal()).isEqualByComparingTo("0.00"); }

    @Test
    void rejectsInvalidItemValues() {
        assertThatIllegalArgumentException().isThrownBy(() -> new OrderItem(new Order(), new Product(), 0, BigDecimal.ONE));
        assertThatIllegalArgumentException().isThrownBy(() -> new OrderItem(new Order(), new Product(), 1, new BigDecimal("-1")));
    }

    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    void persistsExplicitStatusCodes(OrderStatus status) {
        var converter = new OrderStatusConverter();
        assertThat(converter.convertToDatabaseColumn(status)).isEqualTo(status.getCode());
        assertThat(converter.convertToEntityAttribute(status.getCode())).isEqualTo(status);
    }

    @Test
    void rejectsUnknownStatusCodes() { assertThatIllegalArgumentException().isThrownBy(() -> OrderStatus.fromCode(99)); }

    @Test
    void transientEntitiesHaveDistinctIdentity() {
        assertThat(new User()).isNotEqualTo(new User());
        assertThat(new Product()).isNotEqualTo(new Product());
    }
}
