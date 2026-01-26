package com.example.nagoyamesi.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Table(
    name = "favorites",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "store_id"})
)
@Data
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ユーザー
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 店舗
    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
    // お気に入り登録日時
   // @Column(name = "created_at", nullable = false)
    //private LocalDateTime createdAt;
}