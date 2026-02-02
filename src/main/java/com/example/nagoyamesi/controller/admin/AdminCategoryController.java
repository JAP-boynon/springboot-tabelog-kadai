package com.example.nagoyamesi.controller.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyamesi.entity.Category;
import com.example.nagoyamesi.repository.CategoryRepository;
import com.example.nagoyamesi.repository.StoreRepository;

@Controller
public class AdminCategoryController {
	
   

    private final CategoryRepository categoryRepository;
    private final StoreRepository storeRepository;

    public AdminCategoryController(CategoryRepository categoryRepository,
    		StoreRepository storeRepository) {
        this.categoryRepository = categoryRepository;
        this.storeRepository = storeRepository;
    
    }
    
    @GetMapping("/admin")
    public String adminTop() {
        return "admin/index";
    }


    // カテゴリ一覧 + 検索
    @GetMapping("/admin/categories")
    public String index(
            @RequestParam(name = "keyword", required = false) String keyword,
            Model model) {

        List<Category> categories;

        if (keyword == null || keyword.isBlank()) {
            categories = categoryRepository.findAll();
        } else {
            categories = categoryRepository.findByNameContaining(keyword);
        }

        model.addAttribute("categories", categories);
        model.addAttribute("keyword", keyword);

        return "admin/categories/index";
    }
    
 // カテゴリ登録画面
    @GetMapping("/admin/categories/new")
    public String newCategory() {
        return "admin/categories/new";
    }
    // カテゴリ登録処理
    @PostMapping("/admin/categories")
    public String createCategory(
            @RequestParam("name") String name,
            RedirectAttributes redirectAttributes) {

        try {
            Category category = new Category();
            category.setName(name);
            categoryRepository.save(category);

            redirectAttributes.addFlashAttribute(
                "successMessage", "カテゴリを登録しました");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                "errorMessage", "同じカテゴリ名は登録できません");
        }

        return "redirect:/admin/categories";
    }
   
    
 // カテゴリ編集画面
    @GetMapping("/admin/categories/{id}/edit")
    public String editCategory(@PathVariable("id") Long id, Model model) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));

        model.addAttribute("category", category);

        return "admin/categories/edit";
    }
    
 // カテゴリ更新処理
    @PostMapping("/admin/categories/{id}")
    public String updateCategory(
            @PathVariable("id") Long id,
            @RequestParam("name") String name,
            RedirectAttributes redirectAttributes) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));

        try {
            category.setName(name);
            categoryRepository.save(category);

            redirectAttributes.addFlashAttribute(
                "successMessage", "カテゴリを更新しました");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                "errorMessage", "同じカテゴリ名には変更できません");
        }

        return "redirect:/admin/categories";
    }

 // カテゴリ削除
    @PostMapping("/admin/categories/{id}/delete")
    public String deleteCategory(@PathVariable("id") Long id,
    		RedirectAttributes redirectAttributes) {

        Category category = categoryRepository.findById(id)
            
        	.orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));
        
        String categoryName = category.getName();
        
    	/*
        if (storeRepository.existsByCategory_Id(id)) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                "このカテゴリは使用中のため削除できません"
            );
            return "redirect:/admin/categories";
        }
        */

       // categoryRepository.deleteById(id);
       categoryRepository.delete(category);
       redirectAttributes.addFlashAttribute(
    	        "successMessage",
    	        "カテゴリ「" + categoryName + "」を削除しました"
    	    );

       // redirectAttributes.addFlashAttribute("successMessage", "カテゴリを削除しました");

        return "redirect:/admin/categories";
    }
}