package com.example.nagoyamesi.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyamesi.entity.Company;
import com.example.nagoyamesi.repository.CompanyRepository;

@Controller
	@RequestMapping("/admin/company")
	public class AdminCompanyController {

	    private final CompanyRepository companyRepository;

	    public AdminCompanyController(CompanyRepository companyRepository) {
	        this.companyRepository = companyRepository;
	    }

	    // 会社情報表示
	    @GetMapping
	    public String show(Model model) {
	        Company company = companyRepository.findById(1L)
	            .orElseThrow(() -> new IllegalStateException("会社情報が未登録です"));
	        model.addAttribute("company", company);
	        return "admin/company/show";
	    }

	    // 編集画面
	    @GetMapping("/edit")
	    public String edit(Model model) {
	        Company company = companyRepository.findById(1L)
	            .orElseThrow(() -> new IllegalStateException("会社情報が未登録です"));
	        model.addAttribute("company", company);
	        return "admin/company/edit";
	    }

	    // 更新処理
	    @PostMapping
	    public String update(
	        @RequestParam String name,
	        @RequestParam String representative,
	        @RequestParam String address,
	        @RequestParam String business,
	        RedirectAttributes redirectAttributes
	    ) {
	        Company company = companyRepository.findById(1L)
	            .orElseThrow(() -> new IllegalStateException("会社情報が未登録です"));

	        company.setName(name);
	        company.setRepresentative(representative);
	        company.setAddress(address);
	        company.setBusiness(business);

	        companyRepository.save(company);

	        redirectAttributes.addFlashAttribute(
	            "successMessage", "会社情報を更新しました"
	        );

	        return "redirect:/admin/company";
	    }
	}


