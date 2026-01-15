package com.example.nagoyamesi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyamesi.entity.Store;
import com.example.nagoyamesi.repository.StoreRepository;

@Controller
public class StoreController {

    private final StoreRepository storeRepository;

    public StoreController(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    /**
     * トップページ
     */
    @GetMapping("/")
    public String top(Model model) {
    	//評価高いお店
    	model.addAttribute(
    			  "highRatedStores",
    			  storeRepository.findTop6ByOrderByAverageRatingDesc()
    			);
    	//新着のお店
    	model.addAttribute(
    			  "newStores",
    			  storeRepository.findTop6ByOrderByCreatedAtDesc()
    			);
        return "top";
    }
    /**
     * 店舗一覧ページ（検索結果）
     *
     * 検索条件：
     * ①何も入れない 全件表示
     * カテゴリだけ
     *店名検索のみ
     *両方検索
     */
    @GetMapping("/stores")
    public String index(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) Integer price,
        @RequestParam(required = false) String sort,
        @PageableDefault(size = 10) Pageable pageable,
        Model model
    ) {

        /* =========================
           ① 並び替え条件を決める
           ========================= */
    	Sort sortCondition;

    	// デフォルト：新着順
    	if (sort == null || sort.isEmpty() || "new_desc".equals(sort)) {
    	    sortCondition = Sort.by(Sort.Direction.DESC, "createdAt");

    	} else if ("rating_desc".equals(sort)) {
    	    sortCondition = Sort.by(Sort.Direction.DESC, "averageRating");

    	} else if ("price_asc".equals(sort)) {
    	    sortCondition = Sort.by(Sort.Direction.ASC, "price");

    	} else if ("review_desc".equals(sort)) {
    	    sortCondition = Sort.by(Sort.Direction.DESC, "reviewCount");

    	} else {
    	    // 万が一の保険
    	    sortCondition = Sort.by(Sort.Direction.DESC, "createdAt");
    	}

        /* =========================
           ② Pageable に合体させる
           ========================= */
        Pageable sortedPageable =
            PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sortCondition
            );

        /* =========================
           ③ 検索条件の判定
           ========================= */
        Page<Store> storePage;

        boolean hasKeyword = keyword != null && !keyword.isEmpty();
        boolean hasCategory = category != null && !category.isEmpty();
        boolean hasPrice = price != null;

        if (hasKeyword && hasCategory && hasPrice) {
            storePage = storeRepository
                .findByNameContainingAndCategoryNameAndPriceLessThanEqual(
                    keyword, category, price, sortedPageable);

        } else if (hasKeyword && hasCategory) {
            storePage = storeRepository
                .findByNameContainingAndCategoryName(
                    keyword, category, sortedPageable);

        } else if (hasCategory && hasPrice) {
            storePage = storeRepository
                .findByCategoryNameAndPriceLessThanEqual(
                    category, price, sortedPageable);

        } else if (hasKeyword) {
            storePage = storeRepository
                .findByNameContaining(keyword, sortedPageable);

        } else if (hasCategory) {
            storePage = storeRepository
                .findByCategoryName(category, sortedPageable);

        } else if (hasPrice) {
            storePage = storeRepository
            	    .findByPriceLessThanEqual(price, sortedPageable);

        } else {
            storePage = storeRepository.findAll(sortedPageable);
        }

        /* =========================
           ④ View に渡す
           ========================= */
        model.addAttribute("storePage", storePage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("price", price);
        model.addAttribute("sort", sort);

        return "stores/index";
    }

    /**
     * 店舗詳細ページ
     */
    @GetMapping("/stores/{id}")
    public String show(@PathVariable Integer id, Model model) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("店舗が見つかりません"));

        model.addAttribute("store", store);
        return "stores/show";
    }
}