package com.lock.smith.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bill")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "code")
    public String code;

    @Column(name = "customer_id", nullable = false)
    public Long customerId;

    @Column(name = "item_type")
    public String itemType;

    @Column(name = "amount")
    public BigDecimal amount;

    @Column(name = "paid")
    public BigDecimal paid;

    @Column(name = "pay_status")
    public String payStatus;

    @Column(name = "created_at")
    public LocalDateTime createdAt = LocalDateTime.now();
}
