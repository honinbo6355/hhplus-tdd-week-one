package io.hhplus.tdd.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.dto.PointHistoryDto;
import io.hhplus.tdd.dto.UserPointDto;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PointService {
    private UserPointTable userPointTable;
    private PointHistoryTable pointHistoryTable;

    public PointService(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    public UserPointDto charge(Long id, Long amount) {
        // TODO rollback 처리
        UserPoint userPoint = userPointTable.insertOrUpdate(id, amount);
        pointHistoryTable.insert(userPoint.getId(), userPoint.getPoint(), TransactionType.CHARGE, userPoint.getUpdateMillis());
        return new UserPointDto(userPoint);
    }

    public UserPointDto use(Long id, Long amount) {
        // TODO rollback 처리
        UserPoint userPoint = userPointTable.selectById(id);
        UserPoint usedUserPoint = userPoint.usePoint(amount);
        pointHistoryTable.insert(usedUserPoint.getId(), usedUserPoint.getPoint(), TransactionType.USE, usedUserPoint.getUpdateMillis());
        return new UserPointDto(userPointTable.insertOrUpdate(usedUserPoint.getId(), usedUserPoint.getPoint()));
    }

    public UserPointDto point(Long id) {
        return new UserPointDto(userPointTable.selectById(id));
    }

    public List<PointHistoryDto> history(Long id) {
        return pointHistoryTable.selectAllByUserId(id)
                .stream()
                .map(PointHistoryDto::new)
                .collect(Collectors.toList());
    }
}
