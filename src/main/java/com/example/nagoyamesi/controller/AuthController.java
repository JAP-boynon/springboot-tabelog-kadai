package com.example.nagoyamesi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
	@GetMapping("/login")
	public String login() {
		return "auth/login";
	}
	
	@GetMapping("/signup")
    public String signup() {
        return "auth/signup"; // ← 空でOK
    }

    @GetMapping("/password/reset")
    public String passwordReset() {
        return "auth/password_reset"; // ← 空でOK
    }

}
