package com.example.nagoyamesi.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyamesi.entity.Review;
import com.example.nagoyamesi.entity.Store;
import com.example.nagoyamesi.entity.User;
import com.example.nagoyamesi.form.ReservationInputForm;
import com.example.nagoyamesi.repository.StoreRepository;
import com.example.nagoyamesi.security.UserDetailslmpl;
import com.example.nagoyamesi.service.ReservationService;
import com.example.nagoyamesi.service.ReviewService;
import com.example.nagoyamesi.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class StoreController {

    private final StoreRepository storeRepository;
    private final ReviewService reviewService;
    private final ReservationService reservationService;
    private final StripeService stripeService;
   

    public StoreController(StoreRepository storeRepository, ReviewService reviewService,ReservationService reservationService,StripeService stripeService
    	    ) {
        this.storeRepository = storeRepository;
        this.reviewService = reviewService;
        this.reservationService = reservationService;
        this.stripeService = stripeService;
        
        
    }

    /**
     * トップページ
     */
    @GetMapping("/")
    public String top(Model model) {
    	//評価高いお店
    	model.addAttribute(
    			  "highRatedStores",
    			  storeRepository.findTop6ByOrderByAverageRatingDesc()
    			);
    	//新着のお店
    	model.addAttribute(
    			  "newStores",
    			  storeRepository.findTop6ByOrderByCreatedAtDesc()
    			);
        return "top";
    }
    /**
     * 店舗一覧ページ（検索結果）
     *
     * 検索条件：
     * ①何も入れない 全件表示
     * カテゴリだけ
     *店名検索のみ
     *両方検索
     */
    @GetMapping("/stores")
    public String index(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) Integer price,
        @RequestParam(required = false) String sort,
        @PageableDefault(size = 10) Pageable pageable,
        Model model
    ) {

        /* =========================
           ① 並び替え条件を決める
           ========================= */
    	Sort sortCondition;

    	// デフォルト：新着順
    	if (sort == null || sort.isEmpty() || "new_desc".equals(sort)) {
    	    sortCondition = Sort.by(Sort.Direction.DESC, "createdAt");

    	} else if ("rating_desc".equals(sort)) {
    	    sortCondition = Sort.by(Sort.Direction.DESC, "averageRating");

    	} else if ("price_asc".equals(sort)) {
    	    sortCondition = Sort.by(Sort.Direction.ASC, "price");

    	} else if ("review_desc".equals(sort)) {
    	    sortCondition = Sort.by(Sort.Direction.DESC, "reviewCount");

    	} else {
    	    // 万が一の保険
    	    sortCondition = Sort.by(Sort.Direction.DESC, "createdAt");
    	}

        /* =========================
           ② Pageable に合体させる
           ========================= */
        Pageable sortedPageable =
            PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sortCondition
            );

        /* =========================
           ③ 検索条件の判定
           ========================= */
        Page<Store> storePage;

        boolean hasKeyword = keyword != null && !keyword.isEmpty();
        boolean hasCategory = category != null && !category.isEmpty();
        boolean hasPrice = price != null;

        if (hasKeyword && hasCategory && hasPrice) {
            storePage = storeRepository
                .findByNameContainingAndCategoryNameAndPriceLessThanEqual(
                    keyword, category, price, sortedPageable);

        } else if (hasKeyword && hasCategory) {
            storePage = storeRepository
                .findByNameContainingAndCategoryName(
                    keyword, category, sortedPageable);

        } else if (hasCategory && hasPrice) {
            storePage = storeRepository
                .findByCategoryNameAndPriceLessThanEqual(
                    category, price, sortedPageable);

        } else if (hasKeyword) {
            storePage = storeRepository
                .findByNameContaining(keyword, sortedPageable);

        } else if (hasCategory) {
            storePage = storeRepository
                .findByCategoryName(category, sortedPageable);

        } else if (hasPrice) {
            storePage = storeRepository
            	    .findByPriceLessThanEqual(price, sortedPageable);

        } else {
            storePage = storeRepository.findAll(sortedPageable);
        }

        /* =========================
           ④ View に渡す
           ========================= */
        model.addAttribute("storePage", storePage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("price", price);
        model.addAttribute("sort", sort);

        return "stores/index";
    }

    /**
     * 店舗詳細ページ
     */
    @GetMapping("/stores/{id}")
    public String show(@PathVariable Integer id, Model model) {

        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("店舗が見つかりません"));

        // 👇 店舗に紐づくレビュー一覧を取得
        List<Review> reviews = reviewService.findByStore(store);

        model.addAttribute("store", store);
        model.addAttribute("reviews", reviews);
        model.addAttribute("reservationInputForm", new ReservationInputForm());
        model.addAttribute("reservationTimes",
                reservationService.getReservationTimes(store));

        return "stores/show";
    }
    //予約内容確認
    @PostMapping("/stores/{id}/reservations/confirm")
    public String confirm(
            @PathVariable Integer id,
            @Validated @ModelAttribute ReservationInputForm reservationInputForm,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetailslmpl userDetailslmpl,
            HttpServletRequest httpServletRequest,
            Model model
    ) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("店舗が見つかりません"));

        // バリデーションエラー → 店舗詳細に戻す
        if (bindingResult.hasErrors()) {
            model.addAttribute("store", store);
            model.addAttribute("reviews", reviewService.findByStore(store));
            model.addAttribute("reservationTimes",
                    reservationService.getReservationTimes(store));
            return "stores/show";
        }

        // ② ログインユーザー取得
        User user = userDetailslmpl.getUser();

        // ③ Stripe Checkout セッション作成
        String sessionId = stripeService.createStripeSession(
                store,
                user,
                reservationInputForm,
                httpServletRequest
        );

        // ④ confirm.html に渡す
        model.addAttribute("store", store);
        model.addAttribute("reservationInputForm", reservationInputForm);
        model.addAttribute("sessionId", sessionId);

        return "reservations/confirm";
    }
    
   // @PostMapping("/stores/{id}/reservations")
   // public String create(
         //   @PathVariable Integer id,
          //  @ModelAttribute ReservationInputForm reservationInputForm,
          //  @AuthenticationPrincipal UserDetailslmpl userDetailslmpl
  //  ) {
    //    Store store = storeRepository.findById(id)
     //           .orElseThrow(() -> new RuntimeException("店舗が見つかりません"));

     //   User user = userDetailslmpl.getUser();

        // 予約を保存
       // reservationService.create(store, user, reservationInputForm);

        // 一覧へ
      //  return "redirect:/reservations?reserved";
    //}
    
    /**
     * Stripe 決済完了後
     */
    @GetMapping("/stores/{id}/reservations")
    public String success(
            @PathVariable Integer id,
            @RequestParam("session_id") String sessionId,
            @AuthenticationPrincipal UserDetailslmpl userDetailslmpl
    ) throws StripeException {

        User user = userDetailslmpl.getUser();
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("店舗が見つかりません"));

        Session session = Session.retrieve(sessionId);
        PaymentIntent paymentIntent = PaymentIntent.retrieve(session.getPaymentIntent());

        ReservationInputForm form = new ReservationInputForm();
        form.setReservationDate(LocalDate.parse(paymentIntent.getMetadata().get("reservationDate")));
        form.setReservationTime(LocalTime.parse(paymentIntent.getMetadata().get("reservationTime")));
        form.setNumberOfPeople(Integer.parseInt(paymentIntent.getMetadata().get("numberOfPeople")));

       // reservationService.create(store, user, form);

        return "redirect:/reservations?reserved";
    }
   
}