package com.example.nagoyamesi.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyamesi.entity.Favorite;
import com.example.nagoyamesi.entity.Store;
import com.example.nagoyamesi.entity.User;
import com.example.nagoyamesi.repository.StoreRepository;
import com.example.nagoyamesi.security.UserDetailslmpl;
import com.example.nagoyamesi.service.FavoriteService;

import lombok.RequiredArgsConstructor;
    
@Controller
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final StoreRepository storeRepository;

    /**
     * お気に入り登録 / 解除（店舗詳細ページ）
     * @param redirectAttributes 
     */
    @PostMapping("/favorites/{storeId}")
    public String toggleFavorite(
            @PathVariable Integer storeId,
            @AuthenticationPrincipal UserDetailslmpl userDetailslmpl,
            RedirectAttributes redirectAttributes
            ) {
    	
    	 System.out.println("★ toggleFavorite called ★");

        User user = userDetailslmpl.getUser();
        
        System.out.println("user id = " + user.getId());
        System.out.println("store id = " + storeId);
        
        // 店舗取得
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("店舗が存在しません"));

        boolean existed = favoriteService.isFavorite(user, store);
        // お気に入り登録 or 解除
        favoriteService.toggleFavorite(user, store);
        
        if (existed) {
            redirectAttributes.addFlashAttribute(
                "successMessage", "お気に入りを解除しました");
        } else {
            redirectAttributes.addFlashAttribute(
                "successMessage", "お気に入りに登録しました");
        }


        // 元の店舗詳細ページへリダイレクト
        return "redirect:/stores/" + storeId;
    }

    /**
     * お気に入り一覧表示
     */
    @GetMapping("/favorites")
    public String favoriteList(
            @AuthenticationPrincipal UserDetailslmpl userDetailslmpl,
            Model model) {

    	 User user = userDetailslmpl.getUser();
        List<Favorite> favorites = favoriteService.findFavoritesByUser(user);
        model.addAttribute("favorites", favorites);

        return "favorites/index";
    }
    
   
}
