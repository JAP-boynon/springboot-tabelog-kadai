package com.example.nagoyamesi.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	                         Principal principal) {

	        Store store = storeRepository.findById(storeId)
	                .orElseThrow();

	        User user = userRepository.findByEmail(principal.getName())
	                .orElseThrow();

	        Review review = new Review();
	        review.setStore(store);
	        review.setUser(user);
	        review.setRating(rating);
	        review.setComment(comment);

	        reviewService.create(review);

	        return "redirect:/stores/" + storeId;
	    }
	    @PostMapping("/delete/{id}")
	    public String delete(@PathVariable Integer id, Principal principal) {

	        Review review = reviewService.findById(id);
	     // ログインユーザーとレビュー投稿者が一致するか
	        if (!review.getUser().getEmail().equals(principal.getName())) {
	            throw new RuntimeException("他人のレビューは削除できません");
	        }

	        Integer storeId = review.getStore().getId();

	        reviewService.delete(review);

	        return "redirect:/stores/" + storeId;
	    }
	}

