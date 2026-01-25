package com.example.nagoyamesi.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;



@Entity
@Table(name = "reservations")
@Data
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    // 店舗
    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    // ユーザー
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 予約日
    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;

    // 予約時間（30分刻み）
    @Column(name = "reservation_time", nullable = false)
    private LocalTime reservationTime;

    // 人数（最大30人）
    @Column(name = "number_of_people", nullable = false)
    private Integer numberOfPeople;

    // 作成日時
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // 更新日時
    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "stripe_session_id", unique = true)
    private String stripeSessionId;
}
