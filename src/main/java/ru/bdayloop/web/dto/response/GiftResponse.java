package ru.bdayloop.web.dto.response;

import ru.bdayloop.model.Gift;

public record GiftResponse(int id, int userId, String title) {
    public static GiftResponse from(Gift gift) {
        return new GiftResponse(gift.getId(), gift.getUserId(), gift.getTitle());
    }
}
