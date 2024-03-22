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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {
    @Mock
    private UserPointTable userPointTable;
    @Mock
    private PointHistoryTable pointHistoryTable;
    @InjectMocks
    private PointService pointService;

    @Test
    @DisplayName("포인트_충전_성공할경우")
    public void 포인트_충전_성공할경우() {
        // given
        Long id = 1L;
        Long amount = 10000L;

        // when
        when(userPointTable.insertOrUpdate(id, amount)).thenReturn(new UserPoint(id, amount, System.currentTimeMillis()));
        UserPointDto userPointDto = pointService.charge(id, amount);

        // then
        Assertions.assertEquals(id, userPointDto.getId());
        Assertions.assertEquals(amount, userPointDto.getPoint());
    }

    @Test
    @DisplayName("충전금액_0이하일때_포인트_충전_실패할경우")
    public void 충전금액_0이하일때_포인트_충전_실패할경우() {
        // given
        Long id = 1L;
        Long amount = -1000L;

        // when
        CustomException customException = Assertions.assertThrows(CustomException.class, () -> pointService.charge(id, amount));

        // then
        Assertions.assertEquals(ErrorCode.INVALID_PARAMETER, customException.getErrorCode());
    }

    @Test
    @DisplayName("포인트_사용_성공할경우")
    public void 포인트_사용_성공할경우() {
        // given
        Long id = 1L;
        Long amount = 5000L;
        UserPoint userPoint = new UserPoint(id, 15000L, System.currentTimeMillis());

        // when
        when(userPointTable.selectById(id)).thenReturn(userPoint);
        when(userPointTable.insertOrUpdate(id, userPoint.getPoint()-amount)).thenReturn(new UserPoint(id, userPoint.getPoint()-amount, System.currentTimeMillis()));
        UserPointDto userPointDto = pointService.use(id, amount);

        // then
        Assertions.assertEquals(userPoint.getPoint()-amount, userPointDto.getPoint());
    }

    @Test
    @DisplayName("사용할_포인트가_부족할때_포인트_사용_실패할경우")
    public void 사용할_포인트가_부족할때_포인트_사용_실패할경우() {
        // given
        Long id = 1L;
        Long amount = 10000L;
        UserPoint userPoint = new UserPoint(id, 1000L, System.currentTimeMillis());

        // when
        when(userPointTable.selectById(id)).thenReturn(userPoint);
        CustomException customException = Assertions.assertThrows(CustomException.class, () -> pointService.use(id, amount));

        // then
        Assertions.assertEquals(ErrorCode.POINT_SHORTAGE, customException.getErrorCode());
    }

    @Test
    @DisplayName("포인트_조회_성공할경우")
    public void 포인트_조회_성공할경우() {
        // given
        Long id = 1L;
        Long amount = 10000L;
        UserPoint userPoint = new UserPoint(id, amount, System.currentTimeMillis());

        // when
        when(userPointTable.selectById(id)).thenReturn(userPoint);
        UserPointDto userPointDto = pointService.point(id);

        // then
        Assertions.assertEquals(userPoint.getPoint(), userPointDto.getPoint());
    }

    @Test
    @DisplayName("포인트_충전_사용_내역_조회")
    public void 포인트_충전_사용_내역_조회() {
        // given
        Long userId = 1L;
        List<PointHistory> pointHistoryList = new ArrayList<>();
        pointHistoryList.add(new PointHistory(1L, userId, TransactionType.CHARGE, 10000L, System.currentTimeMillis()));
        pointHistoryList.add(new PointHistory(2L, userId, TransactionType.USE, 2000L, System.currentTimeMillis()));
        pointHistoryList.add(new PointHistory(3L, userId, TransactionType.USE, 3000L, System.currentTimeMillis()));

        // when
        when(pointHistoryTable.selectAllByUserId(userId)).thenReturn(pointHistoryList);
        List<PointHistoryDto> pointHistoryDtoList = pointService.history(userId);

        // then
        for (int i=0; i<pointHistoryDtoList.size(); i++) {
            Assertions.assertEquals(pointHistoryList.get(i).getId(), pointHistoryDtoList.get(i).getId());
            Assertions.assertEquals(pointHistoryList.get(i).getUserId(), pointHistoryDtoList.get(i).getUserId());
            Assertions.assertEquals(pointHistoryList.get(i).getAmount(), pointHistoryDtoList.get(i).getAmount());
            Assertions.assertEquals(pointHistoryList.get(i).getType(), pointHistoryDtoList.get(i).getType());
        }
    }
}
