package com.example.nagoyamesi.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.nagoyamesi.entity.Reservation;
import com.example.nagoyamesi.entity.Store;
import com.example.nagoyamesi.entity.User;
import com.example.nagoyamesi.form.ReservationInputForm;
import com.example.nagoyamesi.repository.ReservationRepository;
import com.example.nagoyamesi.repository.StoreRepository;
import com.example.nagoyamesi.repository.UserRepository;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository,
    		StoreRepository storeRepository,
            UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
    }
    
    public void create(Map<String, String> metadata) {

        Integer storeId = Integer.valueOf(metadata.get("storeId"));
        Integer userId = Integer.valueOf(metadata.get("userId"));

        LocalDate reservationDate =
                LocalDate.parse(metadata.get("reservationDate"));

        LocalTime reservationTime =
                LocalTime.parse(metadata.get("reservationTime"));

        Integer numberOfPeople =
                Integer.valueOf(metadata.get("numberOfPeople"));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("店舗が見つかりません"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));

        Reservation reservation = new Reservation();
        reservation.setStore(store);
        reservation.setUser(user);
        reservation.setReservationDate(reservationDate);
        reservation.setReservationTime(reservationTime);
        reservation.setNumberOfPeople(numberOfPeople);

        reservationRepository.save(reservation);
    }

    public List<String> getReservationTimes(Store store) {

        List<String> reservationTimes = new ArrayList<>();

        LocalTime start = store.getOpeningTime();
        LocalTime end   = store.getClosingTime();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        while (start.isBefore(end)) {
            reservationTimes.add(start.format(formatter));
            start = start.plusMinutes(30);
        }

        return reservationTimes;
    }


    public void create(Store store, User user, ReservationInputForm form) {
        Reservation reservation = new Reservation();

        reservation.setStore(store);
        reservation.setUser(user);
        reservation.setReservationDate(form.getReservationDate());
        reservation.setReservationTime(form.getReservationTime());
        reservation.setNumberOfPeople(form.getNumberOfPeople());

        reservationRepository.save(reservation);
    }
}


