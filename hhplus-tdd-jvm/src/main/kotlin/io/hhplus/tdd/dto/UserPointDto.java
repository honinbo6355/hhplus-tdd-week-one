package io.hhplus.tdd.dto;

import io.hhplus.tdd.point.UserPoint;

public class UserPointDto {
    private Long id;
    private Long point;
    private Long updateMillis;

    public UserPointDto(UserPoint userPoint) {
        this.id = userPoint.getId();
        this.point = userPoint.getPoint();
        this.updateMillis = userPoint.getUpdateMillis();
    }

    public Long getId() {
        return id;
    }

    public Long getPoint() {
        return point;
    }

    public Long getUpdateMillis() {
        return updateMillis;
    }
}
