package com.example.nagoyamesi.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyamesi.entity.Store;
import com.example.nagoyamesi.form.StoreEditForm;
import com.example.nagoyamesi.form.StoreRegisterForm;
import com.example.nagoyamesi.repository.StoreRepository;
import com.example.nagoyamesi.service.StoreService;

@Controller
@RequestMapping("/admin/stores")
public class AdminStoreController {

    private final StoreRepository storeRepository;
    private final StoreService storeService;

    public AdminStoreController(StoreRepository storeRepository, StoreService storeService) {
        this.storeRepository = storeRepository;
        this.storeService = storeService;
    }

    // 管理者用 店舗一覧
    @GetMapping
    public String index(Model model) {
        List<Store> stores = storeRepository.findAll();
        model.addAttribute("stores", stores);
        return "admin/stores/index";
    }

    // 管理者用 店舗詳細
    @GetMapping("/{id}")
    public String show(@PathVariable Integer id, Model model) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("店舗が見つかりません"));
        model.addAttribute("store", store);
        return "admin/stores/show";
    }
    
    @GetMapping("/new")
    public String newStore(Model model) {
    	model.addAttribute("storeRegisterForm", new StoreRegisterForm());
    	return "admin/stores/new";
    }
    
    @PostMapping("/create")
    public String create(
            @ModelAttribute @Validated StoreRegisterForm storeRegisterForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "admin/stores/new";
        }

        storeService.create(storeRegisterForm);
        redirectAttributes.addFlashAttribute("successMessage", "店舗を登録しました");

        return "redirect:/admin/stores";
    }
    
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Integer id, Model model) {

        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("店舗が見つかりません"));

        String imageName = store.getImageName();

        StoreEditForm storeEditForm = new StoreEditForm(
                store.getId(),
                store.getName(),
                store.getCategoryName(),
                null, // 画像はファイルなのでnullでOK
                store.getPrice(),
                store.getDescription(),
                store.getBusinessHours(),
                store.getRegularHoliday(),
                store.getPostalCode(),
                store.getAddress(),
                store.getPhoneNumber()
        );

        model.addAttribute("imageName", imageName);
        model.addAttribute("storeEditForm", storeEditForm);

        return "admin/stores/edit";
    }
    
    @PostMapping("/{id}/update")
    public String update(
            @PathVariable Integer id,
            @ModelAttribute @Validated StoreEditForm storeEditForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
    	
    	System.out.println("【DEBUG】path id = " + id);
    	System.out.println("【DEBUG】form id = " + storeEditForm.getId());
    	
        if (bindingResult.hasErrors()) {
            // 編集画面で画像表示したいので、imageNameを再セット
            Store store = storeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("店舗が見つかりません"));
            model.addAttribute("imageName", store.getImageName());
            return "admin/stores/edit";
        }

        storeService.update(storeEditForm);
        redirectAttributes.addFlashAttribute("successMessage", "店舗情報を更新しました。");

        return "redirect:/admin/stores";
    }
    
    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes
    ) {
        storeService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "店舗を削除しました。");
        return "redirect:/admin/stores";
    }
}