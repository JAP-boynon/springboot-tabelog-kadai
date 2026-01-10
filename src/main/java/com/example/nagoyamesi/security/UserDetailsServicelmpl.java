package com.example.nagoyamesi.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.nagoyamesi.entity.User;
import com.example.nagoyamesi.repository.UserRepository;

@Service
public class UserDetailsServicelmpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServicelmpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Spring Security がログイン時に呼び出すメソッド
     * username には login.html の name="username" の値（＝メールアドレス）が入る
     */
    
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        // メールアドレスでユーザーを取得
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("ユーザーが見つかりません"));

        // ロールを GrantedAuthority に変換
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(
                new SimpleGrantedAuthority(user.getRole().getName())
        );

        // UserDetailsImpl に詰めて返す
        return new UserDetailslmpl(user, authorities);
    }
}
