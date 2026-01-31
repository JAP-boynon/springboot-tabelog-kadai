package com.example.nagoyamesi.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "categories")
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
//作成日時
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
//更新日時
    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}