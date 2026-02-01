package com.example.nagoyamesi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "companies")
@Data
public class Company {

    @Id
    private Long id;   // ← 1 固定で使う（設定マスタ）

    @Column(nullable = false)
    private String name;           // 会社名

    private String representative; // 代表者

    private String address;        // 住所

    @Column(columnDefinition = "TEXT")
    private String business;       // 事業内容
}
