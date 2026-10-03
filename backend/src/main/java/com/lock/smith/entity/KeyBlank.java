package com.lock.smith.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "key_blank")
public class KeyBlank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "code")
    public String code;

    @Column(name = "type")
    public String type;

    @Column(name = "stock", nullable = false)
    public Integer stock;

    @Column(name = "threshold", nullable = false)
    public Integer threshold;

    @Column(name = "created_at")
    public LocalDateTime createdAt = LocalDateTime.now();
}
