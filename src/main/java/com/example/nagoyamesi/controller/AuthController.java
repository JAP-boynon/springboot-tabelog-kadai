package com.example.nagoyamesi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyamesi.form.SignupForm;
import com.example.nagoyamesi.service.UserService;

@Controller
public class AuthController {
	private final UserService userService;
	
	public AuthController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping("/login")
	public String login() {
		return "auth/login";
	}
	
	 @GetMapping("/signup")
	    public String signup(Model model) {
	    	model.addAttribute("signupForm", new SignupForm());
	    	return "auth/signup";
	    }
	 
	 @PostMapping("/signup")
	 public String signup(
	         @ModelAttribute @Validated SignupForm signupForm,
	         BindingResult bindingResult,
	         RedirectAttributes redirectAttributes) {

		 
	     //メール重複チェック
	    if (userService.isEmailRegistered(signupForm.getEmail())) {
	         bindingResult.addError(
	             new FieldError(bindingResult.getObjectName(),
	                            "email",
	                           "すでに登録済みのメールアドレスです。")
	        );
	        
	     }

	     // パスワード一致チェック
	     if (!userService.isSamePassword(
	             signupForm.getPassword(),
	             signupForm.getPasswordConfirmation())) {

	         bindingResult.addError(
	             new FieldError(bindingResult.getObjectName(),
	                            "password",
	                            "パスワードが一致しません。")
	         );
	     }

	     if (bindingResult.hasErrors()) {
	         return "auth/signup";
	     }

	     userService.create(signupForm);
	     redirectAttributes.addFlashAttribute("successMessage", "会員登録が完了しました。");

	     return "redirect:/";
	 }

    @GetMapping("/password/reset")
    public String passwordReset() {
        return "auth/password_reset"; // ← 空でOK
    }
    
   

}
