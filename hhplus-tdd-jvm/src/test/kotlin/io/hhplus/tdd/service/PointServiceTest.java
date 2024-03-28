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

/**
 * 서비스 계층의 유닛 테스트이다.
 * 서비스 기능 외의 다른 의존성은 배제한다.
 */
@ExtendWith(MockitoExtension.class)
public class PointServiceTest {
    // 가짜 객체 주입
    @Mock
    private UserPointTable userPointTable;
    // 가짜 객체 주입
    @Mock
    private PointHistoryTable pointHistoryTable;
    // table 객체들 주입
    @InjectMocks
    private PointService pointService;

    /**
     * 작성 이유 : 포인트 충전 성공시 결과값 검증
     */
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

    /**
     * 작성 이유 : 충전금액 0이하일 때 INVALID_PARAMETER 예외 발생할 경우 결과값 검증
     */
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

    /**
     * 작성 이유 : 포인트 사용 성공할 경우 결과값 검증
     */
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

    /**
     * 작성 이유 : 사용할 포인트가 부족할 때 POINT_SHORTAGE 예외 발생할 경우 결과 검증
     */
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

    /**
     * 작성 이유 : 포인트 조회 성공할 경우 결과 검증
     */
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

    /**
     * 작성 이유 : 포인트 내역 조회 성공할 경우 결과 검증
     */
    @Test
    @DisplayName("포인트_내역_조회")
    public void 포인트_내역_조회() {
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
