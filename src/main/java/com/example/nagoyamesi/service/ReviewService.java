package com.example.nagoyamesi.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyamesi.entity.Review;
import com.example.nagoyamesi.entity.Store;
import com.example.nagoyamesi.repository.ReviewRepository;

@Service
public class ReviewService {



	    private final ReviewRepository reviewRepository;
	    private final StoreService storeService;

	    public ReviewService(ReviewRepository reviewRepository,
	                         StoreService storeService) {
	        this.reviewRepository = reviewRepository;
	        this.storeService = storeService;
	    }

	    // レビュー作成
	    @Transactional
	    public void create(Review review) {
	        reviewRepository.save(review);
	        storeService.updateAverageRating(review.getStore());
	    }
	    //レビュー編集
	    @Transactional
	    public void update(Review review) {
	        reviewRepository.save(review);
	        storeService.updateAverageRating(review.getStore());
	    }
	   // レビュー削除
	    @Transactional
	    public void delete(Review review) {
	        Store store = review.getStore();
	        reviewRepository.delete(review);
	        storeService.updateAverageRating(store);
	    }

	    // ★ IDから取得（Controller用）
	    public Review findById(Integer id) {
	        return reviewRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("レビューが見つかりません"));
	    }
	    public List<Review> findByStore(Store store) {
	    	return reviewRepository.findByStoreOrderByCreatedAtDesc(store);
	    }
	}
	    // レビュー削除（あとで使う）
	   // @Transactional
	   // public void delete(Integer reviewId) {
	       // Review review = reviewRepository.findById(reviewId)
	           // .orElseThrow(() -> new RuntimeException("レビューが見つかりません"));

	        //Store store = review.getStore();

	       // reviewRepository.delete(review);
	       // storeService.updateAverageRating(store);
	   // }
	

