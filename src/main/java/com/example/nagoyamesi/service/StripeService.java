package com.example.nagoyamesi.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.nagoyamesi.entity.User;
import com.example.nagoyamesi.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethodCollection;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.servlet.http.HttpServletRequest;


@Service
public class StripeService {

    @Value("${stripe.api-key}")
    private String stripeApiKey;
    
    private final UserRepository userRepository;
    
    public StripeService(UserRepository userRepository) {
    	this.userRepository = userRepository;
    }

    /**
     * サブスクリプション（有料会員登録）用 Checkout Session
     */
    public String createSubscriptionSession(
            User user,
            HttpServletRequest request
    ) {
        Stripe.apiKey = stripeApiKey;
        
        String customerId = getOrCreateCustomer(user);

        String baseUrl =
                request.getScheme() + "://" +
                request.getServerName() + ":" +
                request.getServerPort();

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                        .setCustomer(customerId)
                       // .setCustomer(user.getStripeCustomerId())
                       // .setCustomerEmail(user.getEmail())
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        // Stripeで作成した Price ID
                                        .setPrice("price_1SuXMELMUsOz07kDRmCMN36J")
                                        .setQuantity(1L)
                                        .build()
                        )
                       // .setSuccessUrl(baseUrl + "/subscription/complete")
                       // .setCancelUrl(baseUrl + "/subscription")
                        .setSuccessUrl("http://localhost:8080/")
                        .setCancelUrl("http://localhost:8080/payment")
                        .build();

        try {
            Session session = Session.create(params);
            return session.getUrl(); // Stripe Checkout URL
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String getOrCreateCustomer(User user
    		) {
    	
        Stripe.apiKey = stripeApiKey;

        if (user.getStripeCustomerId() != null) {
            return user.getStripeCustomerId();
        }

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("email", user.getEmail());
            params.put("name", user.getName());

            Customer customer = Customer.create(params);

            // ★ DBに保存
            user.setStripeCustomerId(customer.getId());
            userRepository.save(user);

            return customer.getId();

        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }


 // クレカ情報「表示用」取得
   
 // クレカ情報「表示用」取得
    public PaymentMethod getDefaultPaymentMethod(User user) {
        Stripe.apiKey = stripeApiKey;

        if (user.getStripeCustomerId() == null) {
            return null;
        }

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("customer", user.getStripeCustomerId());
            params.put("type", "card");

            PaymentMethodCollection methods = PaymentMethod.list(params);

            if (methods.getData().isEmpty()) {
                return null;
            }

            return methods.getData().get(0);

        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

   
    
    /**
     * クレジットカード登録・編集（Customer Portal）
     */
    public String createCustomerPortalSession(
            User user,
            HttpServletRequest request
    ) {
        Stripe.apiKey = stripeApiKey;
        
        String customerId = getOrCreateCustomer(user);
        
        // 念のためチェック
       // if (user.getStripeCustomerId() == null) {
        //    throw new RuntimeException("Stripe Customer ID が存在しません");
      //  }


        String baseUrl =
                request.getScheme() + "://" +
                request.getServerName() + ":" +
                request.getServerPort();
        
        
        com.stripe.param.billingportal.SessionCreateParams params =
                com.stripe.param.billingportal.SessionCreateParams.builder()
                        .setCustomer(customerId)
                      // .setCustomer(user.getStripeCustomerId())
                        .setReturnUrl(baseUrl + "/payment")
                        .build();
                        

        try {
            com.stripe.model.billingportal.Session session =
                    com.stripe.model.billingportal.Session.create(params);

            return session.getUrl(); // Customer Portal URL
            
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }
}
	    

	


