package com.example.nagoyamesi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyamesi.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Integer> {

    /* =====================
       検索 + 並び替え対応
       ===================== */

    // 店名
    Page<Store> findByNameContaining(String keyword, Pageable pageable);

    // カテゴリ
    Page<Store> findByCategoryName(String categoryName, Pageable pageable);

    // 価格（〜円まで）
    Page<Store> findByPriceLessThanEqual(Integer price, Pageable pageable);

    // 店名 + カテゴリ
    Page<Store> findByNameContainingAndCategoryName(
        String keyword,
        String categoryName,
        Pageable pageable
    );

    // カテゴリ + 価格
    Page<Store> findByCategoryNameAndPriceLessThanEqual(
        String categoryName,
        Integer price,
        Pageable pageable
    );

    // 店名 + カテゴリ + 価格
    Page<Store> findByNameContainingAndCategoryNameAndPriceLessThanEqual(
        String keyword,
        String categoryName,
        Integer price,
        Pageable pageable
    );
}

