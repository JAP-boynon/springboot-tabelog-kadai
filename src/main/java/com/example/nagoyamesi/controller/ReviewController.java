package com.example.nagoyamesi.controller;

import java.security.Principal;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyamesi.entity.Review;
import com.example.nagoyamesi.entity.Store;
import com.example.nagoyamesi.entity.User;
import com.example.nagoyamesi.repository.StoreRepository;
import com.example.nagoyamesi.repository.UserRepository;
import com.example.nagoyamesi.service.ReviewService;

@Controller
@RequestMapping("/reviews")
public class ReviewController {
	 private final ReviewService reviewService;
	    private final StoreRepository storeRepository;
	    private final UserRepository userRepository;

	    public ReviewController(ReviewService reviewService,
	                            StoreRepository storeRepository,
	                            UserRepository userRepository) {
	        this.reviewService = reviewService;
	        this.storeRepository = storeRepository;
	        this.userRepository = userRepository;
	    }

	    @PostMapping("/create")
	    public String create(@RequestParam Integer storeId,
	                         @RequestParam Integer rating,
	                         @RequestParam(required = false) String comment,
	                         Principal principal,
	                         RedirectAttributes redirectAttributes
               ) {

	        Store store = storeRepository.findById(storeId)
	                .orElseThrow();

	        User user = userRepository.findByEmail(principal.getName())
	                .orElseThrow();
	        
	        // ★ 有料会員チェック
	        if (!user.isPaid()) {
	            redirectAttributes.addFlashAttribute(
	                "errorMessage",
	                "レビュー投稿は有料会員限定の機能です"
	            );
	            return "redirect:/stores/" + storeId;
	        }

	        Review review = new Review();
	        review.setStore(store);
	        review.setUser(user);
	        review.setRating(rating);
	        review.setComment(comment);

	        reviewService.create(review);

	        return "redirect:/stores/" + storeId;
	    }
	    @PostMapping("/delete/{id}")
	    public String delete(@PathVariable Integer id,
	    		Principal principal,
	    		 RedirectAttributes redirectAttributes) {

	        Review review = reviewService.findById(id);
	        User user = review.getUser();
	    

	        Integer storeId = review.getStore().getId();
	        
	        // ⭐ 有料会員チェック
	        if (!user.isPaid()) {
	            redirectAttributes.addFlashAttribute(
	                "errorMessage",
	                "レビューの削除は有料会員限定の機能です"
	            );
	            return "redirect:/stores/" + storeId;
	        }

	        // 投稿者本人チェック
	        if (!user.getEmail().equals(principal.getName())) {
	            throw new AccessDeniedException("このレビューは削除できません");
	        }



	        reviewService.delete(review);
	        redirectAttributes.addFlashAttribute(
	                "successMessage",
	                "レビューを削除しました"
	            );


	        return "redirect:/stores/" + storeId;
	    }
	}

