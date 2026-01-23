package com.example.nagoyamesi.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.nagoyamesi.entity.Store;
import com.example.nagoyamesi.entity.User;
import com.example.nagoyamesi.form.ReservationInputForm;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionRetrieveParams;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class StripeService {

    @Value("${stripe.api-key}")
    private String stripeApiKey;

    private final ReservationService reservationService;

    public StripeService(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // Stripe Checkout セッションを作成して sessionId を返す
    public String createStripeSession(
            Store store,
            User user,
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
                                                        // 仮：1,000円 × 人数（必要ならあとで計算式変える）
                                                        .setUnitAmount(1000L * form.getNumberOfPeople())
                                                        .setCurrency("jpy")
                                                        .build()
                                        )
                                        .setQuantity(1L)
                                        .build()
                        )
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(requestUrl.replace("/confirm", "") + "?session_id={CHECKOUT_SESSION_ID}")
                        .setCancelUrl(requestUrl.replace("/confirm", ""))
                        .setPaymentIntentData(
                                SessionCreateParams.PaymentIntentData.builder()
                                        .putMetadata("storeId", store.getId().toString())
                                        .putMetadata("userId", user.getId().toString())
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

    // Webhook: checkout.session.completed を受けたあと、metadataから予約をDB保存
    public void processSessionCompleted(Event event) {
        Optional<StripeObject> optionalStripeObject =
                event.getDataObjectDeserializer().getObject();

        optionalStripeObject.ifPresentOrElse(stripeObject -> {
            Session session = (Session) stripeObject;

            SessionRetrieveParams params =
                    SessionRetrieveParams.builder()
                            .addExpand("payment_intent")
                            .build();

            try {
                session = Session.retrieve(session.getId(), params, null);

                Map<String, String> metadata =
                        session.getPaymentIntentObject().getMetadata();

                reservationService.create(metadata);

                System.out.println("予約一覧ページの登録処理が成功しました。");
                System.out.println("Stripe API Version: " + event.getApiVersion());
                System.out.println("stripe-java Version: " + Stripe.VERSION);

            } catch (StripeException e) {
                e.printStackTrace();
            }

        }, () -> {
            System.out.println("予約一覧ページの登録処理が失敗しました。");
        });
    }
}