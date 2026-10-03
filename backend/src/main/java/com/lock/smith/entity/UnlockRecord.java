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
@Table(name = "unlock_record")
public class UnlockRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "code")
    public String code;

    @Column(name = "kind")
    public String kind;

    @Column(name = "technician")
    public String technician;

    @Column(name = "arrive_at")
    public LocalDateTime arriveAt;

    @Column(name = "finish_at")
    public LocalDateTime finishAt;

    @Column(name = "fee")
    public BigDecimal fee;

    @Column(name = "success")
    public Boolean success;

    @Column(name = "created_at")
    public LocalDateTime createdAt = LocalDateTime.now();
}
