package com.example.nagoyamesi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.nagoyamesi.entity.Review;
import com.example.nagoyamesi.entity.Store;
import com.example.nagoyamesi.entity.User;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

   
    List<Review> findByStoreOrderByCreatedAtDesc(Store store);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.store = :store")
    Double findAverageRatingByStore(@Param("store") Store store);
    
    boolean existsByUserAndStore(User user, Store store);

    Optional<Review> findByUserAndStore(User user, Store store);

}
   
    
