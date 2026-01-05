package com.example.nagoyamesi.form;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StoreRegisterForm {
	@NotBlank(message = "店舗名を入力してください。")
	private String name;
	
	private MultipartFile imageFile;
	
	@NotBlank(message = "カテゴリを入力してください。")
	private String categoryName;
	
	@NotNull(message = "価格を入力してください。")
	@Min(value = 1, message = "価格は1円以上にしてください。")
	private Integer price;
	
	@NotBlank(message = "説明を入力してください。")
	private String description;
	
	@NotBlank(message = "営業時間を入力してください。")
	private String businessHours;
	
	@NotBlank(message = "定休日を入力してください。")
	private String regularHoliday;
	
	@NotBlank(message = "郵便番号を入力してください。")
	private String postalCode;
	
	@NotBlank(message = "住所を入力してください。")
	private String address;
	
	@NotBlank(message = "電話番号を入力してください。")
	private String phoneNumber;
	

}
