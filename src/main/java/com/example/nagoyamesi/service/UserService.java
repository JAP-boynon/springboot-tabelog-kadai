package com.example.nagoyamesi.service;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyamesi.entity.Role;
import com.example.nagoyamesi.entity.User;
import com.example.nagoyamesi.form.SignupForm;
import com.example.nagoyamesi.repository.RoleRepository;
import com.example.nagoyamesi.repository.UserRepository;

import jakarta.annotation.PostConstruct;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	
	public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}
	// 🔥🔥🔥 ここを一時的に追加 🔥🔥🔥
    @PostConstruct
    public void test() {
        System.out.println("🔑 BCrypt(password) = "
                + passwordEncoder.encode("password"));
    }
	
	@Transactional
	public User create(SignupForm signupForm) {
		
		  System.out.println("🔥 create() 開始");
		  
		User user = new User();
		
		Role role = roleRepository.findByName("ROLE_GENERAL")
			    .orElseThrow(() -> new RuntimeException("ROLE_GENERAL が存在しません"));
				
		user.setName(signupForm.getName());
		user.setFurigana(signupForm.getFurigana());
		user.setPostalCode(signupForm.getPostalCode());
		user.setAddress(signupForm.getAddress());
		user.setPhoneNumber(signupForm.getPhoneNumber());
		user.setEmail(signupForm.getEmail());
		user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
		user.setRole(role);
		user.setEnabled(false);
		
		 System.out.println("🔥 user save 直前：");
		
		return userRepository.save(user);
	}
	
	//メールアドレスが登録済みかどうかチェックする
	public boolean isEmailRegistered(String email) {
	    System.out.println("★★ email check: " + email);
	    return userRepository.findByEmail(email).isPresent();
	}
	
	//パスワードとパスワード（確認用）の入力値が一致するかどうかチェックする
	public boolean isSamePassword(String password, String passwordConfirmation) {
		return password.equals(passwordConfirmation);
	}
	
	//ユーザーを有効にする
	public void enableUser(User user) {
		user.setEnabled(true);
		userRepository.save(user);
	}
}
