package io.hhplus.tdd.point

import io.hhplus.tdd.exception.CustomException
import io.hhplus.tdd.exception.ErrorCode

data class UserPoint(
    val id: Long,
    val point: Long,
    val updateMillis: Long
) {
    fun usePoint(amount: Long): UserPoint {
        var newPoint = this.point - amount
        if (newPoint < 0) {
            throw CustomException(ErrorCode.POINT_SHORTAGE)
        }
        return this.copy(point = newPoint)
    }
}
