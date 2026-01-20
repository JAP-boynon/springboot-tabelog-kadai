package com.example.nagoyamesi.service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.nagoyamesi.entity.Reservation;
import com.example.nagoyamesi.entity.Store;
import com.example.nagoyamesi.entity.User;
import com.example.nagoyamesi.form.ReservationInputForm;
import com.example.nagoyamesi.repository.ReservationRepository;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
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


