package com.example.nagoyamesi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyamesi.entity.Favorite;
import com.example.nagoyamesi.entity.Store;
import com.example.nagoyamesi.entity.User;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // すでにお気に入りしているか判定用
    Optional<Favorite> findByUserAndStore(User user, Store store);

    // ログインユーザーのお気に入り一覧取得
    List<Favorite> findByUser(User user);

    // お気に入り解除用（Serviceで使う場合）
    void deleteByUserAndStore(User user, Store store);
}