package io.hhplus.tdd.dto;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;

public class PointHistoryDto {
    private Long id;
    private Long userId;
    private TransactionType type;
    private Long amount;
    private Long timeMillis;

    public PointHistoryDto(PointHistory pointHistory) {
        this.id = pointHistory.getId();
        this.userId = pointHistory.getUserId();
        this.type = pointHistory.getType();
        this.amount = pointHistory.getAmount();
        this.timeMillis = pointHistory.getTimeMillis();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public TransactionType getType() {
        return type;
    }

    public Long getAmount() {
        return amount;
    }

    public Long getTimeMillis() {
        return timeMillis;
    }
}
