package io.hhplus.tdd;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.dto.PointHistoryDto;
import io.hhplus.tdd.dto.UserPointDto;
import io.hhplus.tdd.exception.CustomException;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.service.PointService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 통합 테스트는 실제 환경과 동일하게 진행하는 테스트이다.
 * 각 계층의 의존성을 주입받고 테스트한다.
 */
@SpringBootTest
public class IntegrationTest {

    // bean 의존성 주입
    @Autowired
    private PointService pointService;

    // bean 의존성 주입
    @Autowired
    private UserPointTable userPointTable;

    @Nested
    @DisplayName("포인트 충전시")
    class Charge {

        /**
         * 작성 이유 : 클라이언트에서 잘못된 금액을 전달하는 경우 예외 발생 테스트
         */
        @Test
        public void _0이하_금액_충전_시도할경우() {
            Assertions.assertThrows(CustomException.class, () -> {
                pointService.charge(1L, -1000L);
            });
        }

        /**
         * 작성 이유 : 충전이 정상적으로 수행되는 경우 성공 테스트
         */
        @Test
        public void 충전_성공할경우() {
            pointService.charge(1L, 5000L);

            UserPoint userPoint = userPointTable.selectById(1L);

            Assertions.assertEquals(1L, userPoint.getId());
            Assertions.assertEquals(5000L, userPoint.getPoint());
        }
    }

    @Nested
    @DisplayName("포인트 사용시")
    class Use {

        /**
         * 작성 이유 : 잔고보다 초과된 금액을 사용할 경우 예외 발생 테스트
         */
        @Test
        public void 잔고_부족할경우() {
            pointService.charge(1L, 5000L);

            Assertions.assertThrows(CustomException.class, () -> {
                pointService.use(1L, 6000L);
            });
        }

        /**
         * 작성 이유 : 정상적으로 사용할 경우 성공 테스트
         */
        @Test
        public void 사용_성공할경우() {
            userPointTable.insertOrUpdate(1L, 5000L);
            pointService.use(1L, 3000L);
            UserPoint usedUserPoint = userPointTable.selectById(1L);

            Assertions.assertEquals(2000L, usedUserPoint.getPoint());
        }
    }

    @Nested
    @DisplayName("포인트 조회시")
    class Point {

        /**
         * 작성 이유 : 정상적으로 조회할 경우 성공 테스트
         */
        @Test
        public void 조회_성공할경우() {
            pointService.charge(1L, 5000L);

            UserPointDto userPointDto = pointService.point(1L);

            Assertions.assertEquals(1L, userPointDto.getId());
            Assertions.assertEquals(5000L, userPointDto.getPoint());
        }
    }

    @Nested
    @DisplayName("포인트 내역 조회시")
    class History {

        /**
         * 작성 이유 : 정상적으로 내역 조회할 경우 성공 테스트
         */
        @Test
        public void 내역_조회_성공할경우() {
            pointService.charge(1L, 5000L);
            pointService.use(1L, 3000L);

            List<PointHistoryDto> pointHistoryDtos = pointService.history(1L);

            Assertions.assertEquals(2L, pointHistoryDtos.size());
        }
    }

    @Nested
    @DisplayName("멀티 스레드 요청시")
    class Concurrency {

        /**
         * 작성 이유 : 분산 환경이 아닌경우(단일 인스턴스), synchronized로 동기화 처리 되는지 테스트
         * 시나리오 : 쓰레드 10개에서 (1000원 업데이트 => 500원 사용)
         */
        @Test
        public void 동시에_여러건_충전_사용_요청시_성공_테스트() throws InterruptedException {
            int numThreads = 10;

            CountDownLatch latch = new CountDownLatch(numThreads);
            ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

            for (int i=0; i<numThreads; i++) {
                executorService.submit(() -> {
                    try {
                        pointService.charge(1L, 1000L);
                        pointService.use(1L, 500L);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                });
            }
            executorService.shutdown();
            latch.await();
            UserPoint userPoint = userPointTable.selectById(1L);
            Assertions.assertEquals(500L, userPoint.getPoint());
        }

        /**
         * 작성 이유 : 분산 환경이 아닌경우(단일 인스턴스), synchronized로 동기화 처리 되는지 테스트
         * 시나리오 : 5000원 충전 - 쓰레드 10개에서 (500원 사용)
         */
        @Test
        public void 동시에_여러건_사용_요청시_성공_테스트() throws InterruptedException {
            int numThreads = 10;

            CountDownLatch latch = new CountDownLatch(numThreads);
            ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
            pointService.charge(1L, 5000L);

            for (int i=0; i<numThreads; i++) {
                executorService.submit(() -> {
                    try {
                        pointService.use(1L, 500L);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                });
            }
            executorService.shutdown();
            latch.await();
            UserPoint userPoint = userPointTable.selectById(1L);
            Assertions.assertEquals(0L, userPoint.getPoint());
        }
    }
}
