package org.example.mapreservation.hairshop.domain;

import lombok.Builder;

import java.util.List;

@Builder
public record HairShopResponse(
        Long shopId,
        String shopName,
        String latitude,
        String longitude,
        List<String> images
) {
    public static HairShopResponse from(HairShop hairShop) {
        return new HairShopResponse(
                hairShop.getId(),
                hairShop.getName(),
                hairShop.getLatitude(),
                hairShop.getLongitude(),
                hairShop.getImageUrls());
    }
}
