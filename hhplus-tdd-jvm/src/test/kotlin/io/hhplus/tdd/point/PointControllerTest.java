package io.hhplus.tdd.point;

import io.hhplus.tdd.dto.PointHistoryDto;
import io.hhplus.tdd.dto.UserPointDto;
import io.hhplus.tdd.exception.CustomException;
import io.hhplus.tdd.exception.ErrorCode;
import io.hhplus.tdd.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointController.class)
public class PointControllerTest {

    @MockBean
    private PointService pointService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("포인트_조회_성공할경우")
    public void 포인트_조회_성공할경우() throws Exception {
        // given
        Long id = 1L;
        UserPointDto userPointDto = new UserPointDto(new UserPoint(id, 0L, System.currentTimeMillis()));

        // when
        when(pointService.point(id)).thenReturn(userPointDto);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/point/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.point").value(0L));
    }

    @Test
    @DisplayName("포인트_조회_실패할경우")
    public void 포인트_조회_실패할경우() throws Exception {
        // given
        Long id = 1L;

        // when
        when(pointService.point(id)).thenThrow(NullPointerException.class);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/point/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(500))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("에러가 발생했습니다."));
    }

    @Test
    @DisplayName("포인트_충전_사용_내역_조회_성공할경우")
    public void 포인트_충전_사용_내역_조회() throws Exception {
        // given
        Long userId = 1L;
        List<PointHistoryDto> pointHistoryDtos = new ArrayList<>();
        pointHistoryDtos.add(new PointHistoryDto(new PointHistory(1L, userId, TransactionType.CHARGE, 10000L, System.currentTimeMillis())));
        pointHistoryDtos.add(new PointHistoryDto(new PointHistory(2L, userId, TransactionType.USE, 2000L, System.currentTimeMillis())));
        pointHistoryDtos.add(new PointHistoryDto(new PointHistory(3L, userId, TransactionType.USE, 3000L, System.currentTimeMillis())));

        // when
        when(pointService.history(userId)).thenReturn(pointHistoryDtos);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/point/" + userId + "/histories")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }

    @Test
    @DisplayName("포인트_충전_사용_내역_조회_실패할경우")
    public void 포인트_충전_사용_내역_조회_실패할경우() throws Exception {
        // given
        Long userId = 1L;

        // when
        when(pointService.history(userId)).thenThrow(NullPointerException.class);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/point/" + userId + "/histories")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(500))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("에러가 발생했습니다."));
    }

    @Test
    @DisplayName("포인트_충전_성공할경우")
    public void 포인트_충전_성공할경우() throws Exception {
        // given
        Long userId = 1L;
        Long amount = 10000L;

        // when
        when(pointService.charge(userId, amount)).thenReturn(new UserPointDto(new UserPoint(userId, amount, System.currentTimeMillis())));

        // then
        mockMvc.perform(MockMvcRequestBuilders.patch("/point/" + userId + "/charge")
                        .content(String.valueOf(amount))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.point").value(amount));
    }

    @Test
    @DisplayName("충전금액_0이하일때_포인트_충전_실패할경우")
    public void 충전금액_0이하일때_포인트_충전_실패할경우() throws Exception {
        // given
        Long userId = 1L;
        Long amount = -10000L;

        // when
        when(pointService.charge(userId, amount)).thenThrow(new CustomException(ErrorCode.INVALID_PARAMETER));

        // then
        mockMvc.perform(MockMvcRequestBuilders.patch("/point/" + userId + "/charge")
                        .content(String.valueOf(amount))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ErrorCode.INVALID_PARAMETER.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(ErrorCode.INVALID_PARAMETER.getMessage()));
    }

    @Test
    @DisplayName("포인트_사용_성공할경우")
    public void 포인트_사용_성공할경우() throws Exception {
        // given
        Long userId = 1L;
        Long amount = 10000L;
        Long usedAmount = 5000L;

        // when
        when(pointService.use(userId, amount)).thenReturn(new UserPointDto(new UserPoint(userId, usedAmount, System.currentTimeMillis())));

        // then
        mockMvc.perform(MockMvcRequestBuilders.patch("/point/" + userId + "/use")
                        .content(String.valueOf(amount))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.point").value(usedAmount));
    }
    @Test
    @DisplayName("사용할_포인트가_부족할때_포인트_사용_실패할경우")
    public void 사용할_포인트가_부족할때_포인트_사용_실패할경우() throws Exception {
        // given
        Long userId = 1L;
        Long amount = 10000L;

        // when
        when(pointService.use(userId, amount)).thenThrow(new CustomException(ErrorCode.POINT_SHORTAGE));

        // then
        mockMvc.perform(MockMvcRequestBuilders.patch("/point/" + userId + "/use")
                        .content(String.valueOf(amount))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ErrorCode.POINT_SHORTAGE.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(ErrorCode.POINT_SHORTAGE.getMessage()));
    }
}
