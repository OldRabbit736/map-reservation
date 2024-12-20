package org.example.mapreservation.reservation.application;

import lombok.RequiredArgsConstructor;
import org.example.mapreservation.customer.application.repository.CustomerRepository;
import org.example.mapreservation.customer.domain.Customer;
import org.example.mapreservation.exception.CustomException;
import org.example.mapreservation.exception.ErrorCode;
import org.example.mapreservation.hairshop.application.repository.HairShopRepository;
import org.example.mapreservation.hairshop.domain.HairShop;
import org.example.mapreservation.reservation.application.repository.HairShopReservationRepository;
import org.example.mapreservation.reservation.application.service.HairShopReservationService;
import org.example.mapreservation.reservation.application.service.TimeProvider;
import org.example.mapreservation.reservation.domain.HairShopReservation;
import org.example.mapreservation.reservation.domain.HairShopReservationCreateResponse;
import org.example.mapreservation.reservation.domain.HairShopReservationCreateRequest;
import org.example.mapreservation.reservation.domain.HairShopReservationResponse;
import org.example.mapreservation.reservation.domain.ReservationStatus;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class HairShopReservationServiceImpl implements HairShopReservationService {

    private final HairShopReservationRepository hairShopReservationRepository;
    private final HairShopRepository hairShopRepository;
    private final CustomerRepository customerRepository;
    private final RedissonClient redissonClient;
    private final TransactionTemplate transactionTemplate;
    private final TimeProvider timeProvider;

    /**
     * 분산락 적용으로 동시에 같은 헤어샵, 같은 시간에 예약이 되지 않도록 처리.
     */
    @Override
    public HairShopReservationCreateResponse createReservation(
            Long shopId, String username, HairShopReservationCreateRequest request) {

        // redisson을 이용한 분산락 적용으로 "헤어샵 id + 예약 시간" 조합이 유일하게 예약될 수 있도록 함
        // TODO: 분산락을 재사용 가능하게, 깔끔하게 적용할 수 있는 법 찾기. (wrapping method or aop or facade ...?)
        String lockKey = String.format("%s:%s", shopId, request.reservationTime());
        RLock lock = redissonClient.getLock(lockKey);

        // 커밋 전에 잠금이 풀리면 race condition이 발생해 정합성에 문제가 발생한다.
        // 커밋 후에 잠금을 풀도록 하기 위해, 트랜잭션 실행을 분산락 실행으로 감쌌다.
        // https://helloworld.kurly.com/blog/distributed-redisson-lock/
        try {
            boolean available = lock.tryLock(5L, 3L, TimeUnit.SECONDS);
            if (!available) {
                throw new CustomException(ErrorCode.LCK_CANNOT_ACQUIRE_LOCK, "잠시 후 다시 시도해 주세요.");
            }
            Long reservationId = transactionTemplate.execute(status ->
                    createReservationInternal(shopId, username, timeProvider.getCurrentDateTime(), request));
            return new HairShopReservationCreateResponse(reservationId);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    @Transactional
    @Override
    public ReservationStatus getReservationStatus(Long shopId, LocalDate targetDate) {
        HairShop hairShop = hairShopRepository.findById(shopId)
                .orElseThrow(() -> new CustomException(ErrorCode.HS_NOT_FOUND));

        List<HairShopReservation> reservations = hairShopReservationRepository
                .findByHairShopAndTargetDate(hairShop, targetDate);

        // TODO: 영업 시작, 종료 시간은 헤어샵 마다, 기준 날짜마다 다르게 가져갈 수 있도록 기능 넣기. 일단 고정된 값으로 전달.
        LocalTime openingTime = LocalTime.of(10, 0);
        LocalTime closingTime = LocalTime.of(20, 0);
        return ReservationStatus.from(targetDate, openingTime, closingTime, reservations);
    }

    @Transactional
    @Override
    public HairShopReservationResponse getReservation(Long reservationId, String username) {
        HairShopReservation hairShopReservation = hairShopReservationRepository
                .findByIdAndCustomerEmail(reservationId, username)
                .orElseThrow(() -> new CustomException(ErrorCode.HSR_NOT_FOUND));
        return HairShopReservationResponse.from(hairShopReservation);
    }

    @Transactional
    @Override
    public Slice<HairShopReservationResponse> getReservations(String username, Pageable pageable) {
        Slice<HairShopReservation> slice = hairShopReservationRepository.findByCustomerEmail(username, pageable);
        return slice.map(HairShopReservationResponse::from);
    }

    @Transactional
    @Override
    public void cancelReservation(Long reservationId, String username, LocalDateTime currentDateTime) {
        // TODO: currentDateTime을 TimeProvider로 처리하기
        HairShopReservation hairShopReservation = hairShopReservationRepository.findByIdAndCustomerEmail(reservationId, username)
                .orElseThrow(() -> new CustomException(ErrorCode.HSR_NOT_FOUND));

        hairShopReservation.cancel(currentDateTime);
    }

    /**
     * 헤어샵 시술 예약 셍성 내부 메소드
     */
    private Long createReservationInternal(
            Long shopId, String username, LocalDateTime currentDateTime, HairShopReservationCreateRequest request) {

        // TODO: currentDateTime을 TimeProvider로 처리하기
        isValidReservationTime(currentDateTime, request.reservationTime());

        Customer customer = customerRepository.findByEmail(username)
                .orElseThrow(() -> new CustomException(ErrorCode.CUST_NOT_FOUND));

        HairShop hairShop = hairShopRepository.findById(shopId)
                .orElseThrow(() -> new CustomException(ErrorCode.HS_NOT_FOUND));

        HairShopReservation hairShopReservation = HairShopReservation.builder()
                .customer(customer)
                .hairShop(hairShop)
                .reservationTime(request.reservationTime())
                .build();

        hairShopReservationRepository.findByHairShopAndReservationTimeAndReservationStatus(
                        hairShop, request.reservationTime(), HairShopReservation.Status.RESERVED
                )
                .ifPresent(r -> {
                    throw new CustomException(ErrorCode.HSR_ALREADY_TAKEN_RESERVATION_TIME);
                });

        return hairShopReservationRepository.save(hairShopReservation).getId();
    }

    private static void isValidReservationTime(LocalDateTime currentDateTime, LocalDateTime reservationTime) {
        if (currentDateTime.isAfter(reservationTime)) {
            throw new CustomException(ErrorCode.HSR_OLD_RESERVATION_TIME);
        }
        List<Integer> allowedMinutes = List.of(0, 30);
        if (!allowedMinutes.contains(reservationTime.getMinute())) {
            throw new CustomException(ErrorCode.HSR_INVALID_RESERVATION_TIME);
        }
        if (reservationTime.getSecond() != 0 || reservationTime.getNano() != 0) {
            throw new CustomException(ErrorCode.HSR_INVALID_RESERVATION_TIME);
        }
    }

}
