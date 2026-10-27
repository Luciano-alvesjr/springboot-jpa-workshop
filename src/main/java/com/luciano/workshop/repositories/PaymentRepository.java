package com.luciano.workshop.repositories;

import com.luciano.workshop.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {}
