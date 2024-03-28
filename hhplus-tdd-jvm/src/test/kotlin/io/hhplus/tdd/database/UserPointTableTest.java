package io.hhplus.tdd.database;

import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

/**
 * 영속성 계층의 유닛 테스트이다.
 * 영속성 계층에서는 의존하고 있는 계층이 없다.
 */
@ExtendWith(MockitoExtension.class)
public class UserPointTableTest {
    // 가짜 객체 주입
    @Mock
    private UserPointTable userPointTable;

    /**
     * 작성 이유 : 포인트 조회 성공시 결과 검증
     */
    @Test
    @DisplayName("포인트_조회")
    public void 포인트_조회() {
        // given
        Long id = 1L;
        UserPoint userPoint = new UserPoint(id, 10000L, System.currentTimeMillis());

        // when
        when(userPointTable.selectById(id)).thenReturn(userPoint);
        UserPoint resultUserPoint = userPointTable.selectById(id);

        // then
        Assertions.assertEquals(userPoint.getId(), resultUserPoint.getId());
        Assertions.assertEquals(userPoint.getPoint(), resultUserPoint.getPoint());
    }

    /**
     * 작성 이유 : 포인트 수정 성공시 결과 검증
     */
    @Test
    @DisplayName("포인트_수정")
    public void 포인트_수정() {
        // given
        Long id = 1L;
        Long amount = 10000L;
        UserPoint userPoint = new UserPoint(id, amount, System.currentTimeMillis());

        // when
        when(userPointTable.insertOrUpdate(id, amount)).thenReturn(userPoint);
        UserPoint resultUserPoint = userPointTable.insertOrUpdate(id, amount);

        // then
        Assertions.assertEquals(userPoint.getId(), resultUserPoint.getId());
        Assertions.assertEquals(userPoint.getPoint(), resultUserPoint.getPoint());
    }
}
