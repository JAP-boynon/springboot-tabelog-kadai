package com.example.nagoyamesi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                // ★ 一般ユーザーが見るページ
                .requestMatchers(
                    "/",
                    "/stores/**",
                    "/storage/**",   // ← 画像表示に必須
                    "/css/**",
                    "/js/**",
                    "/images/**"
                ).permitAll()

                // ★ 管理画面はログイン必須（今は仮）
                .requestMatchers("/admin/**").permitAll()

                .anyRequest().authenticated()
            )

            // 今はログイン機能未完成なので一旦OFF
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
