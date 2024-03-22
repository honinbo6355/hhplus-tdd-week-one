package io.hhplus.tdd.database;

import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserPointTableTest {
    @Mock
    private UserPointTable userPointTable;

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

    @Test
    @DisplayName("포인트_충전")
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
