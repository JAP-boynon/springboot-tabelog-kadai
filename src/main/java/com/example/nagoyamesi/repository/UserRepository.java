package com.example.nagoyamesi.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyamesi.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
    
    // ★ 26章で追加する検索＋ページング用
    Page<User> findByNameLikeOrFuriganaLike(
        String nameKeyword,
        String furiganaKeyword,
        Pageable pageable
    );
}
