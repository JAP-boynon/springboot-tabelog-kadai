package com.example.nagoyamesi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.nagoyamesi.entity.Reservation;
import com.example.nagoyamesi.entity.User;
import com.example.nagoyamesi.repository.ReservationRepository;
import com.example.nagoyamesi.security.UserDetailslmpl;

@Controller
public class ReservationController {
	private final ReservationRepository reservationRepository;
	
	public ReservationController(ReservationRepository reservationRepository) {
		this.reservationRepository = reservationRepository;
	}
	
	/**
	 *予約一覧（ログインユーザー）
	 */
	@GetMapping("/reservations")
	public String index(
	        @AuthenticationPrincipal UserDetailslmpl userDetailsImpl,
	        @PageableDefault(
	            page = 0,
	            size = 10,
	            sort = "createdAt",
	            direction = Sort.Direction.DESC
	        ) Pageable pageable,
	        Model model) {

	    User user = userDetailsImpl.getUser();

	    Page<Reservation> reservationPage =
	            reservationRepository.findByUserOrderByCreatedAtDesc(user, pageable);

	    model.addAttribute("reservationPage", reservationPage);

	    return "reservations/index";
	}
	
	@PostMapping("/reservations/{id}/delete")
	public String delete(
	        @PathVariable Integer id,
	        @AuthenticationPrincipal UserDetailslmpl userDetailslmpl) {

	    User user = userDetailslmpl.getUser();

	    Reservation reservation = reservationRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("予約が存在しません"));

	    //  ログインユーザー本人の予約かチェック（超重要）
	    if (!reservation.getUser().getId().equals(user.getId())) {
	        throw new RuntimeException("不正な操作です");
	    }

	    reservationRepository.delete(reservation);

	    return "redirect:/reservations?canceled";
	}
	

}
