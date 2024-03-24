package org.example.apiserver.hairshop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.apiserver.common.Address;
import org.example.apiserver.owner.domain.Owner;
import org.example.apiserver.reservation.domain.HairShopReservation;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class HairShop {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long name;

    @Embedded
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @OneToMany(mappedBy = "hairShop")
    private List<HairShopReservation> hairShopReservations = new ArrayList<>();

}
