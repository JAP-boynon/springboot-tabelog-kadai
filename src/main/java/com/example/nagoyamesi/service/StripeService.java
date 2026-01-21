package com.example.nagoyamesi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.nagoyamesi.entity.Store;
import com.example.nagoyamesi.form.ReservationInputForm;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class StripeService {

    @Value("${stripe.api-key}")
    private String stripeApiKey;

    public String createStripeSession(
            Store store,
            ReservationInputForm form,
            HttpServletRequest request
    ) {
        Stripe.apiKey = stripeApiKey;

        String requestUrl = request.getRequestURL().toString();

        SessionCreateParams params =
            SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addLineItem(
                    SessionCreateParams.LineItem.builder()
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                .setProductData(
                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(store.getName())
                                        .build()
                                )
                                // 仮：1,000円 × 人数
                                .setUnitAmount(1000L * form.getNumberOfPeople())
                                .setCurrency("jpy")
                                .build()
                        )
                        .setQuantity(1L)
                        .build()
                )
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(
                	    requestUrl.replace("/confirm", "") + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(requestUrl.replace("/confirm", ""))
                .setPaymentIntentData(
                    SessionCreateParams.PaymentIntentData.builder()
                        .putMetadata("storeId", store.getId().toString())
                        .putMetadata("reservationDate", form.getReservationDate().toString())
                        .putMetadata("reservationTime", form.getReservationTime().toString())
                        .putMetadata("numberOfPeople", form.getNumberOfPeople().toString())
                        .build()
                )
                .build();

        try {
            Session session = Session.create(params);
            return session.getId();
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }
}

