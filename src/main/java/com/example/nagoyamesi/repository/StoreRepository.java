package com.example.nagoyamesi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyamesi.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Integer> {

    // 店名検索
    List<Store> findByNameContaining(String keyword);
    
    // カテゴリ検索
    List<Store>  findByCategoryName(String categoryName);
    
    //　店舗名+カテゴリ検索
    List<Store> findByNameContainingAndCategoryName(String name, String categoryName);

   
}
