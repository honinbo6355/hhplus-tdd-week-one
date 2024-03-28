package io.hhplus.tdd.database;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 * 영속성 계층의 유닛 테스트이다.
 * 영속성 계층에서는 의존하고 있는 계층이 없다.
 */
@ExtendWith(MockitoExtension.class)
public class PointHistoryTableTest {

    // 가짜 객체 주입
    @Mock
    private PointHistoryTable pointHistoryTable;

    /**
     * 작성 이유 : 포인트 내역 저장시 결과 검증
     */
    @Test
    @DisplayName("포인트_내역_저장")
    public void 포인트_내역_저장() {
        // given
        Long id = 1L;
        Long userId = 1L;
        Long amount = 10000L;
        TransactionType transactionType = TransactionType.CHARGE;
        long timeMillis = System.currentTimeMillis();
        PointHistory pointHistory = new PointHistory(id, userId, transactionType, amount, timeMillis);

        // when
        when(pointHistoryTable.insert(userId, amount, transactionType, timeMillis)).thenReturn(pointHistory);
        PointHistory resultPointHistory = pointHistoryTable.insert(userId, amount, transactionType, timeMillis);

        // then
        Assertions.assertEquals(pointHistory.getId(), resultPointHistory.getId());
        Assertions.assertEquals(pointHistory.getUserId(), resultPointHistory.getUserId());
        Assertions.assertEquals(pointHistory.getType(), resultPointHistory.getType());
        Assertions.assertEquals(pointHistory.getAmount(), resultPointHistory.getAmount());
    }

    /**
     * 작성 이유 : 포인트 내역 조회시 결과 검증
     */
    @Test
    @DisplayName("포인트_내역_조회")
    public void 포인트_내역_조회() {
        // given
        Long userId = 1L;
        List<PointHistory> pointHistoryList = new ArrayList<>();
        pointHistoryList.add(new PointHistory(1L, userId, TransactionType.CHARGE, 1000L, System.currentTimeMillis()));
        pointHistoryList.add(new PointHistory(2L, userId, TransactionType.CHARGE, 5000L, System.currentTimeMillis()));
        pointHistoryList.add(new PointHistory(3L, userId, TransactionType.USE, 2000L, System.currentTimeMillis()));

        // when
        when(pointHistoryTable.selectAllByUserId(userId)).thenReturn(pointHistoryList);
        List<PointHistory> resultPointHistoryList = pointHistoryTable.selectAllByUserId(userId);

        // then
        Assertions.assertEquals(pointHistoryList.size(), resultPointHistoryList.size());
    }
}
