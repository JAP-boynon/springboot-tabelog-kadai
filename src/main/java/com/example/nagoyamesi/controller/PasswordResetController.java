package com.example.nagoyamesi.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyamesi.entity.PasswordResetToken;
import com.example.nagoyamesi.entity.User;
import com.example.nagoyamesi.repository.PasswordResetTokenRepository;
import com.example.nagoyamesi.repository.UserRepository;
@Transactional
@Controller
public class PasswordResetController {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetController(
            UserRepository userRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            JavaMailSender mailSender,
            PasswordEncoder passwordEncoder
            ) {

        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * パスワード再設定メール入力画面
     */
    @GetMapping("/password/reset")
    public String showResetForm() {
        return "password/reset";
    }

    /**
     * パスワード再設定メール送信
     */
    @PostMapping("/password/reset")
    public String sendResetMail(
            @RequestParam("email") String email,
            Model model) {
    	
    	System.out.println("=== sendResetMail called ===");

        User user = userRepository.findByEmail(email).orElse(null);

        // セキュリティ観点：存在しないメールでも同じメッセージを返す
        if (user == null) {
            model.addAttribute("message",
                    "パスワード再設定用のメールを送信しました");
            return "password/reset";
        }

        // 既存トークン削除（1ユーザー1トークン）
        passwordResetTokenRepository.deleteByUser(user);

        // トークン生成
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setToken(token);
        resetToken.setExpiresAt(LocalDateTime.now().plusHours(1));

        passwordResetTokenRepository.save(resetToken);

        // 再設定URL
        String resetUrl =
                "http://localhost:8080/password/reset/form/" + token;

        // メール送信
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("noreply@nagoyameshi.local");
        mail.setTo(user.getEmail());
        mail.setSubject("【NAGOYAMESHI】パスワード再設定");
        mail.setText(
                "以下のリンクからパスワードを再設定してください。\n\n"
                + resetUrl + "\n\n"
                + "※このリンクは1時間で無効になります。"
        );

        
        System.out.println("=== before mailSender.send ===");

        try {
            mailSender.send(mail);
            System.out.println("=== after mailSender.send ===");
        } catch (Exception e) {
            System.out.println("=== mailSender.send ERROR ===");
            e.printStackTrace();
        }

        model.addAttribute("message",
                "パスワード再設定用のメールを送信しました");
        return "password/reset";
    }

    /**
     * パスワード再設定画面（トークン付きURL）
     */
    @GetMapping("/password/reset/form/{token}")
    public String showUpdateForm(
            @PathVariable String token,
            Model model) {

        PasswordResetToken resetToken =
                passwordResetTokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException("トークンが無効です"));

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("トークンの有効期限が切れています");
        }

        model.addAttribute("token", token);
        return "password/update";
    }

    /**
     * パスワード更新処理
     */
    @PostMapping("/password/reset/update/{token}")
    public String updatePassword(
            @PathVariable String token,
            @RequestParam("password") String password,
            Model model) {

        PasswordResetToken resetToken =
                passwordResetTokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException("トークンが無効です"));

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("トークンの有効期限が切れています");
        }

        User user = resetToken.getUser();

        // パスワードは必ずハッシュ化
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        // トークンは使い捨て
        passwordResetTokenRepository.delete(resetToken);

        return "password/complete";
    }

    
    
}
