package com.example.nagoyamesi.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyamesi.entity.Favorite;
import com.example.nagoyamesi.entity.Store;
import com.example.nagoyamesi.entity.User;
import com.example.nagoyamesi.repository.FavoriteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    /**
     * お気に入り登録 or 解除
     */
    @Transactional
    public void toggleFavorite(User user, Store store) {

        favoriteRepository.findByUserAndStore(user, store)
            .ifPresentOrElse(
                // すでにお気に入り → 解除
                favorite -> favoriteRepository.delete(favorite),

                // 未登録 → 登録
                () -> {
                    Favorite favorite = new Favorite();
                    favorite.setUser(user);
                    favorite.setStore(store);
                    favorite.setCreatedAt(LocalDateTime.now());
                    favoriteRepository.save(favorite);
                }
            );
    }

    /**
     * お気に入りしているか判定
     */
    public boolean isFavorite(User user, Store store) {
        return favoriteRepository.findByUserAndStore(user, store).isPresent();
    }

    /**
     * ログインユーザーのお気に入り一覧取得
     */
    public List<Favorite> findFavoritesByUser(User user) {
        return favoriteRepository.findByUser(user);
    }
}