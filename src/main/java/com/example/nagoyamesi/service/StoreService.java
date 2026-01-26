package com.example.nagoyamesi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.nagoyamesi.entity.Store;
import com.example.nagoyamesi.form.StoreEditForm;
import com.example.nagoyamesi.form.StoreRegisterForm;
import com.example.nagoyamesi.repository.ReviewRepository;
import com.example.nagoyamesi.repository.StoreRepository;

@Service
public class StoreService {

    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository; 

    public StoreService(StoreRepository storeRepository,
    		ReviewRepository reviewRepository) {
        this.storeRepository = storeRepository;
        this.reviewRepository = reviewRepository;
    }
    

    @Transactional
    public void create(StoreRegisterForm form) {

        Store store = new Store();
        MultipartFile imageFile = form.getImageFile();

        if (imageFile != null && !imageFile.isEmpty()) {
            String originalFileName = imageFile.getOriginalFilename();
            String newFileName = generateNewFileName(originalFileName);
            Path filePath = Paths.get("src/main/resources/static/storage/" + newFileName);
            copyImageFile(imageFile, filePath);
            store.setImageName(newFileName);
        }

        store.setName(form.getName());
        store.setCategoryName(form.getCategoryName());
        store.setPrice(form.getPrice());
        store.setDescription(form.getDescription());
        store.setBusinessHours(form.getBusinessHours());
        store.setRegularHoliday(form.getRegularHoliday());
        store.setPostalCode(form.getPostalCode());
        store.setAddress(form.getAddress());
        store.setPhoneNumber(form.getPhoneNumber());

        storeRepository.save(store);
    }

    private String generateNewFileName(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf("."));
        return UUID.randomUUID().toString() + extension;
    }

    private void copyImageFile(MultipartFile imageFile, Path filePath) {
        try {
            Files.copy(imageFile.getInputStream(), filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Transactional
    public void update(StoreEditForm form) {

    	  System.out.println("【DEBUG】update id = " + form.getId());

    	
        Store store = storeRepository.findById(form.getId())
                .orElseThrow(() -> new RuntimeException("店舗が見つかりません"));

        MultipartFile imageFile = form.getImageFile();

        // 画像が選ばれたときだけ更新（選ばれてないなら今の画像を維持）
        if (imageFile != null && !imageFile.isEmpty()) {
            String originalFileName = imageFile.getOriginalFilename();
            String newFileName = generateNewFileName(originalFileName);
            Path filePath = Paths.get("src/main/resources/static/storage/" + newFileName);
            copyImageFile(imageFile, filePath);
            store.setImageName(newFileName);
        }

        store.setName(form.getName());
        store.setCategoryName(form.getCategoryName());
        store.setPrice(form.getPrice());
        store.setDescription(form.getDescription());
        store.setBusinessHours(form.getBusinessHours());
        store.setRegularHoliday(form.getRegularHoliday());
        store.setPostalCode(form.getPostalCode());
        store.setAddress(form.getAddress());
        store.setPhoneNumber(form.getPhoneNumber());

        storeRepository.save(store);
    }
    @Transactional
    public void delete(Integer id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("店舗が見つかりません"));

        storeRepository.delete(store);
    }
    
    //  レビュー
    @Transactional
    public void updateAverageRating(Store store) {
        Double avg = reviewRepository.findAverageRatingByStore(store);
        store.setAverageRating(avg != null ? avg : 0.0);
        storeRepository.save(store);
    }
    
    /**
     * 店舗詳細取得（お気に入り判定などで使用）
     */
    @Transactional(readOnly = true)
    public Store findById(Integer id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("店舗が存在しません"));
    }

}
