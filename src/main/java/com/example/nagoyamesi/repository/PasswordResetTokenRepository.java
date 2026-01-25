package com.example.nagoyamesi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyamesi.entity.PasswordResetToken;
import com.example.nagoyamesi.entity.User;

public interface PasswordResetTokenRepository
extends JpaRepository<PasswordResetToken, Integer> {

Optional<PasswordResetToken> findByToken(String token);

void deleteByUser(User user);
}
