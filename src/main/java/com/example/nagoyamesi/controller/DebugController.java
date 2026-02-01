package com.example.nagoyamesi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.nagoyamesi.entity.User;
import com.example.nagoyamesi.repository.UserRepository;
	

	@RestController
	public class DebugController {

	    private final UserRepository userRepository;

	    public DebugController(UserRepository userRepository) {
	        this.userRepository = userRepository;
	    }

	    @GetMapping("/debug/users")
	    public List<User> debugUsers() {
	        return userRepository.findAll();
	    }
	}


