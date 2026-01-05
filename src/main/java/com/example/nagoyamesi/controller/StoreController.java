package com.example.nagoyamesi.controller;

import java.util.List;

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
     * ・店舗名入力
     * ・カテゴリ選択
     * ・検索ボタン
     */
    @GetMapping("/")
    public String top() {
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
            Model model
    ) {
        List<Store> stores;

        boolean hasKeyword = keyword != null && !keyword.isEmpty();
        boolean hasCategory = category != null && !category.isEmpty();

        if (hasKeyword && hasCategory) {
            // 店舗名 + カテゴリ
            stores = storeRepository
                    .findByNameContainingAndCategoryName(keyword, category);

        } else if (hasKeyword) {
            // 店舗名のみ
            stores = storeRepository.findByNameContaining(keyword);

        } else if (hasCategory) {
            // カテゴリのみ
            stores = storeRepository.findByCategoryName(category);

        } else {
            // 条件なし
            stores = storeRepository.findAll();
        }

        // 検索条件をビューに返す（教材③）
        model.addAttribute("stores", stores);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);

        return "stores/index";
    }
    
    @GetMapping("/stores/{id}")
    public String show(@PathVariable Integer id, Model model) {
    	Store store = storeRepository.findById(id).orElseThrow(() ->  new RuntimeException("店舗が見つかりません"));
    	
    	model.addAttribute("store", store);
    	return "stores/show";
    }
    }