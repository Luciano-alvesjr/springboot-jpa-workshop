package com.luciano.workshop.config;

import com.luciano.workshop.entities.*;
import com.luciano.workshop.entities.enums.OrderStatus;
import com.luciano.workshop.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Configuration
@Profile("test")
public class TestConfig implements CommandLineRunner {
    private final UserRepository users;
    private final CategoryRepository categories;
    private final ProductRepository products;
    private final OrderRepository orders;
    private final OrderItemRepository items;
    private final PasswordEncoder passwords;

    public TestConfig(UserRepository users, CategoryRepository categories, ProductRepository products,
                      OrderRepository orders, OrderItemRepository items, PasswordEncoder passwords) {
        this.users = users;
        this.categories = categories;
        this.products = products;
        this.orders = orders;
        this.items = items;
        this.passwords = passwords;
    }

    @Override
    @Transactional
    public void run(String... args) {
        User maria = new User("Maria Brown", "maria@gmail.com", "988888888", passwords.encode("123456"));
        User alex = new User("Alex Green", "alex@gmail.com", "977777777", passwords.encode("123456"));
        users.saveAll(List.of(maria, alex));
        Category electronics = new Category("Electronics");
        Category books = new Category("Books");
        Category computers = new Category("Computers");
        categories.saveAll(List.of(electronics, books, computers));
        Product lord = new Product("The Lord of the Rings", "Lorem ipsum dolor sit amet, consectetur.", new BigDecimal("90.50"), "");
        Product tv = new Product("Smart TV", "Nulla eu imperdiet purus. Maecenas ante.", new BigDecimal("2190.00"), "");
        Product mac = new Product("Macbook Pro", "Nam eleifend maximus tortor, at mollis.", new BigDecimal("1250.00"), "");
        Product pc = new Product("PC Gamer", "Donec aliquet odio ac rhoncus cursus.", new BigDecimal("1200.00"), "");
        Product rails = new Product("Rails for Dummies", "Cras fringilla convallis sem vel faucibus.", new BigDecimal("100.99"), "");
        lord.getCategories().add(books);
        tv.getCategories().add(electronics);
        mac.getCategories().addAll(List.of(electronics, computers));
        pc.getCategories().add(computers);
        rails.getCategories().add(books);
        products.saveAll(List.of(lord, tv, mac, pc, rails));
        Order first = new Order(Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, maria);
        Order second = new Order(Instant.parse("2019-07-21T03:42:10Z"), OrderStatus.WAITING_PAYMENT, alex);
        Order third = new Order(Instant.parse("2019-07-22T15:21:22Z"), OrderStatus.WAITING_PAYMENT, maria);
        orders.saveAll(List.of(first, second, third));
        items.saveAll(List.of(
            new OrderItem(first, lord, 2, lord.getPrice()),
            new OrderItem(first, mac, 1, mac.getPrice()),
            new OrderItem(second, mac, 2, mac.getPrice()),
            new OrderItem(third, rails, 2, rails.getPrice())));
        first.setPayment(new Payment(Instant.parse("2019-06-20T21:53:07Z"), first));
        orders.save(first);
    }
}
