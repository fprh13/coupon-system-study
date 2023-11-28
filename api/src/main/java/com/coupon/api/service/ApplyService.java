package com.coupon.api.service;

import com.coupon.api.domain.Coupon;
import com.coupon.api.producer.CouponCreateProducer;
import com.coupon.api.repository.AppliedUserRepository;
import com.coupon.api.repository.CouponCountRepository;
import com.coupon.api.repository.CouponRepository;
import org.springframework.stereotype.Service;

@Service
public class ApplyService {

    private final CouponRepository couponRepository;

    private final CouponCountRepository couponCountRepository;

    private final CouponCreateProducer couponCreateProducer;

    private final AppliedUserRepository appliedUserRepository;

    public ApplyService(CouponRepository couponRepository,
                        CouponCountRepository couponCountRepository,
                        CouponCreateProducer couponCreateProducer,
                        AppliedUserRepository appliedUserRepository) {
        this.couponRepository = couponRepository;
        this.couponCountRepository = couponCountRepository;
        this.couponCreateProducer = couponCreateProducer;
        this.appliedUserRepository = appliedUserRepository;
    }

    /**
     * 첫 apply 메소드 / 레이스 컨디션 발생
     */
    public void applyV1(Long userId) {
        long count = couponRepository.count();
        if (count > 100) {
            return;
        }
        couponRepository.save(new Coupon(userId));
    }

    /**
     * Redis incr 명령어를 이용하여 해결 할 메서드
     */
    public void applyV2(Long userId) {
        // 구폰을 발급하기 전에 redis 에 +1을 해준다.
        Long count = couponCountRepository.increment();
        if (count > 100) {
            return;
        }
        couponRepository.save(new Coupon(userId));
    }

    /**
     * Redis + kafka를 이용하기
     */
    public void apply(Long userId) {
        Long apply = appliedUserRepository.add(userId);

        // 중복 값있으면 반환
        if (apply != 1) {
            return;
        }

        Long count = couponCountRepository.increment();
        if (count > 1000) {
            return;
        }
        // 프로듀서가 topic 에 유저 아이디를 넘긴다.
        couponCreateProducer.create(userId);
    }
}
