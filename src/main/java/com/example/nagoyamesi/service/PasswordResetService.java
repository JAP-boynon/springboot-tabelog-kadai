package com.example.nagoyamesi.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.nagoyamesi.entity.PasswordResetToken;
import com.example.nagoyamesi.entity.User;
import com.example.nagoyamesi.repository.PasswordResetTokenRepository;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
   

    public PasswordResetService(
            PasswordResetTokenRepository tokenRepository
            ) {
        this.tokenRepository = tokenRepository;
     
    }

    /**
     * パスワードリセット用トークンを作成
     */
    public PasswordResetToken createToken(User user) {

        // ① 既存トークン削除（再発行対策）
        tokenRepository.deleteByUser(user);

        // ② トークン生成
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setToken(token);
        resetToken.setExpiresAt(LocalDateTime.now().plusHours(1));

        return tokenRepository.save(resetToken);
    }

    /**
     * トークンから有効な PasswordResetToken を取得
     */
    public PasswordResetToken getValidToken(String token) {

        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("トークンが無効です"));

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("トークンの有効期限が切れています");
        }

        return resetToken;
    }

    /**
     * トークン削除（パスワード変更後）
     */
    public void deleteToken(PasswordResetToken token) {
        tokenRepository.delete(token);
    }
}
