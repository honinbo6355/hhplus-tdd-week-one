package io.hhplus.tdd.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.dto.PointHistoryDto;
import io.hhplus.tdd.dto.UserPointDto;
import io.hhplus.tdd.exception.CustomException;
import io.hhplus.tdd.exception.ErrorCode;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import org.springframework.stereotype.Service;

import java.util.Comparator;
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

    public synchronized UserPointDto charge(Long id, Long amount) {
        if (amount <= 0) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
        UserPoint userPoint = userPointTable.insertOrUpdate(id, amount);
        pointHistoryTable.insert(userPoint.getId(), userPoint.getPoint(), TransactionType.CHARGE, userPoint.getUpdateMillis());
        return new UserPointDto(userPoint);
    }

    public synchronized UserPointDto use(Long id, Long amount) {
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
                .sorted(Comparator.comparingLong(PointHistory::getId))
                .map(PointHistoryDto::new)
                .collect(Collectors.toList());
    }
}
