package com.lock.smith.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "key_blank_use")
public class KeyBlankUse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "key_blank_id", nullable = false)
    public Long keyBlankId;

    @Column(name = "qty", nullable = false)
    public Integer qty;

    @Column(name = "use_date")
    public LocalDate useDate;

    @Column(name = "created_at")
    public LocalDateTime createdAt = LocalDateTime.now();
}
