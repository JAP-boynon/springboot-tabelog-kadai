package com.example.nagoyamesi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.nagoyamesi.entity.Review;
import com.example.nagoyamesi.entity.Store;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findByStore(Store store);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.store = :store")
    Double findAverageRatingByStore(@Param("store") Store store);
}
   
    
