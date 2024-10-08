package org.example.mapreservation.reservation.dto;

import org.example.mapreservation.hairshop.dto.HairShopDto;
import org.example.mapreservation.reservation.domain.HairShopReservation;

import java.time.LocalDateTime;

/**
 * 헤어샵 예약 정보
 *
 * @param reservationId   예약 id
 * @param username        예약 유저명
 * @param hairShopDto     헤어샵 정보
 * @param reservationTime 예약 시간
 * @param status          예약 상태
 */
public record HairShopReservationDto(
        Long reservationId,
        String username,
        HairShopDto hairShopDto,
        LocalDateTime reservationTime,
        HairShopReservation.Status status

) {
    public static HairShopReservationDto from(
            HairShopReservation hairShopReservation) {

        return new HairShopReservationDto(
                hairShopReservation.getId(),
                hairShopReservation.getCustomer().getEmail(),
                HairShopDto.from(hairShopReservation.getHairShop()),
                hairShopReservation.getReservationTime(),
                hairShopReservation.getReservationStatus()
        );
    }
}
