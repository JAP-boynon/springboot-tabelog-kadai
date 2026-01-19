package com.example.nagoyamesi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyamesi.entity.Reservation;
import com.example.nagoyamesi.entity.Store;
import com.example.nagoyamesi.entity.User;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {


	 // ユーザー用（今使う）
    Page<Reservation> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    // 店舗・管理者用（今は未使用）
    List<Reservation> findByStore(Store store);
}
