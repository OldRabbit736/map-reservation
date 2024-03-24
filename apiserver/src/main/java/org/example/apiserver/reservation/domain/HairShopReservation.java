package org.example.apiserver.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import org.example.apiserver.hairshop.domain.HairShop;
import org.example.apiserver.member.domain.Member;

import java.time.LocalDateTime;

@Entity
public class HairShopReservation {

    @Id @GeneratedValue
    private Long id;

    // 예약 손님
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member member;

    // 예약 받은 헤어샵
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private HairShop hairShop;

    // 예약 시간
    @Column(nullable = false)
    private LocalDateTime reservationTime;

}
