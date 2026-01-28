package com.example.nagoyamesi.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyamesi.entity.User;
import com.example.nagoyamesi.repository.UserRepository;
import com.example.nagoyamesi.security.UserDetailslmpl;

@Controller
@RequestMapping("/subscription")
public class SubscriptionController {
	
	private final UserRepository userRepository;

    public SubscriptionController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

	

	    @GetMapping
	    public String index(@AuthenticationPrincipal UserDetailslmpl userDetails) {
	        User user = userDetails.getUser();

	        // すでに有料会員ならトップへ
	        if (user.isPaid()) {
	            return "redirect:/";
	        }

	        return "subscription/index";
	    }

	    @GetMapping("/cancel")
	    public String cancelConfirm(@AuthenticationPrincipal UserDetailslmpl userDetails) {
	        User user = userDetails.getUser();

	        if (!user.isPaid()) {
	            return "redirect:/";
	        }

	        return "subscription/cancel";
	    }
	    

	    @PostMapping("/cancel")
	    public String cancelSubscription(
	            @AuthenticationPrincipal UserDetailslmpl userDetails,
	            RedirectAttributes redirectAttributes) {

	        User user = userDetails.getUser();
	        user.setPaid(false);
	        userRepository.save(user);
	        
	        // ⭐ フラッシュメッセージ
	        redirectAttributes.addFlashAttribute(
	            "successMessage",
	            "有料プランを解約しました"
	        );

	        return "redirect:/?canceled";
	    }
	    
	    @PostMapping("/activate")
	    public String activate(
	            @AuthenticationPrincipal UserDetailslmpl userDetails) {

	        User user = userDetails.getUser();
	        user.setPaid(true);
	        userRepository.save(user);
	        


	        return "redirect:/?activated";
	    }
	}


