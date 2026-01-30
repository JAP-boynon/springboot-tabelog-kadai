package com.example.nagoyamesi.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.nagoyamesi.entity.User;
import com.example.nagoyamesi.security.UserDetailslmpl;
import com.example.nagoyamesi.service.StripeService;
import com.stripe.model.PaymentMethod;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/payment")
public class PaymentController {
	
	

	    private final StripeService stripeService;

	    public PaymentController(StripeService stripeService) {
	        this.stripeService = stripeService;
	    }
	    
	    
	 // 表示用（クレカ情報を見る）
	    @GetMapping
	    public String show(
	        @AuthenticationPrincipal UserDetailslmpl userDetails,
	        Model model
	    ) {
	        User user = userDetails.getUser();

	        PaymentMethod paymentMethod =
	            stripeService.getDefaultPaymentMethod(user);

	        model.addAttribute("paymentMethod", paymentMethod);
	        return "payment/show";
	    }

	    // 編集用（Stripe Customer Portal へ）
	    @GetMapping("/method")
	    public String edit(
	        @AuthenticationPrincipal UserDetailslmpl userDetails,
	        HttpServletRequest request
	    ) {
	        User user = userDetails.getUser();

	        String url =
	            stripeService.createCustomerPortalSession(user, request);

	        return "redirect:" + url;
	    }

	    /*
	    
	    @GetMapping("/payment")
	    public String payment(
	            @AuthenticationPrincipal UserDetailslmpl userDetails,
	            Model model
	    ) {
	        User user = userDetails.getUser();

	        PaymentMethod paymentMethod =
	                stripeService.getDefaultPaymentMethod(user);

	        model.addAttribute("paymentMethod", paymentMethod);
	        return "payment/show";
	    }
	    */

	    /**
	     * クレジットカード登録・編集
	     
	    @GetMapping("/method")
	    public String paymentMethod(
	            @AuthenticationPrincipal UserDetailslmpl userDetails,
	            HttpServletRequest request
	    ) {
	        User user = userDetails.getUser();

	        // Stripe Customer Portal へリダイレクト
	        String url = stripeService.createCustomerPortalSession(user, request);

	        return "redirect:" + url;
	    }
	    */
	    
	}


