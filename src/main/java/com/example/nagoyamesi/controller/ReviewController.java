package com.example.nagoyamesi.controller;

import java.security.Principal;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyamesi.entity.Review;
import com.example.nagoyamesi.entity.Store;
import com.example.nagoyamesi.entity.User;
import com.example.nagoyamesi.repository.ReviewRepository;
import com.example.nagoyamesi.repository.StoreRepository;
import com.example.nagoyamesi.repository.UserRepository;
import com.example.nagoyamesi.service.ReviewService;

@Controller
@RequestMapping("/reviews")
public class ReviewController {
	private final ReviewService reviewService;
	private final StoreRepository storeRepository;
	private final UserRepository userRepository;
	private final ReviewRepository reviewRepository;

	public ReviewController(ReviewService reviewService,
			StoreRepository storeRepository,
			UserRepository userRepository,
			ReviewRepository reviewRepository) {
		this.reviewService = reviewService;
		this.storeRepository = storeRepository;
		this.userRepository = userRepository;
		this.reviewRepository = reviewRepository;
	}

	@PostMapping("/create")
	public String create(@RequestParam Integer storeId,
			@RequestParam Integer rating,
			@RequestParam(required = false) String comment,
			Principal principal,
			RedirectAttributes redirectAttributes) {
		// ⭐ 未ログインチェック
	    if (principal == null) {
	        redirectAttributes.addFlashAttribute(
	                "errorMessage",
	                "レビュー投稿にはログインが必要です");
	        return "redirect:/login";
	    }
//店舗取得
		Store store = storeRepository.findById(storeId)
				.orElseThrow();
//ログインユーザー取得
		User user = userRepository.findByEmail(principal.getName())
				.orElseThrow();
	    
		// ★ 有料会員チェック
		if (!user.isPaid()) {
			redirectAttributes.addFlashAttribute(
					"errorMessage",
					"レビュー投稿は有料会員限定の機能です");
			return "redirect:/stores/" + storeId;
		}
		
		// ⭐ 1店舗1レビュー制限
	    if (reviewRepository.existsByUserAndStore(user, store)) {
	        redirectAttributes.addFlashAttribute(
	                "errorMessage",
	                "この店舗にはすでにレビューを投稿しています");
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
	
	@GetMapping("/new/{storeId}")
	public String newReview(
	        @PathVariable Integer storeId,
	        Principal principal,
	        RedirectAttributes redirectAttributes,
	        Model model) {

	    // 未ログイン
	    if (principal == null) {
	        redirectAttributes.addFlashAttribute(
	                "errorMessage",
	                "レビュー投稿にはログインが必要です");
	        return "redirect:/login";
	    }

	    User user = userRepository.findByEmail(principal.getName())
	            .orElseThrow();

	    // 無料会員
	    if (!user.isPaid()) {
	        redirectAttributes.addFlashAttribute(
	                "errorMessage",
	                "レビュー投稿は有料会員限定の機能です");
	        return "redirect:/stores/" + storeId;
	    }

	    Store store = storeRepository.findById(storeId)
	            .orElseThrow();

	    model.addAttribute("store", store);
	    return "reviews/new";
	}
	
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable Integer id,
	                   Principal principal,
	                   Model model) {

	    Review review = reviewService.findById(id);

	    // 投稿者本人チェック
	    if (!review.getUser().getEmail().equals(principal.getName())) {
	        throw new AccessDeniedException("編集できません");
	    }

	    model.addAttribute("review", review);
	    return "reviews/edit";
	}
	
	@PostMapping("/update")
	public String update(@RequestParam Integer id,
	                     @RequestParam Integer rating,
	                     @RequestParam String comment,
	                     Principal principal) {

	    Review review = reviewService.findById(id);

	    if (!review.getUser().getEmail().equals(principal.getName())) {
	        throw new AccessDeniedException("更新できません");
	    }

	    review.setRating(rating);
	    review.setComment(comment);

	    reviewService.update(review);

	    return "redirect:/stores/" + review.getStore().getId();
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
					"レビューの削除は有料会員限定の機能です");
			return "redirect:/stores/" + storeId;
		}

		// 投稿者本人チェック
		if (!user.getEmail().equals(principal.getName())) {
			throw new AccessDeniedException("このレビューは削除できません");
		}

		reviewService.delete(review);
		redirectAttributes.addFlashAttribute(
				"successMessage",
				"レビューを削除しました");

		return "redirect:/stores/" + storeId;
	}
}
