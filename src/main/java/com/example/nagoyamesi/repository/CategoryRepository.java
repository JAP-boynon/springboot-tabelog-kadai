package com.example.nagoyamesi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyamesi.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // カテゴリ名で部分一致検索（管理画面用）
    List<Category> findByNameContaining(String keyword);
    
   

}