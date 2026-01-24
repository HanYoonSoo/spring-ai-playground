package com.hanyoonsoo.springaiplayground.global.common.response

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import org.springframework.http.HttpStatus
import java.time.Instant

@JsonPropertyOrder("code", "message")
class ApiResponse<T> (
    val code: String = "0000",
    val httpStatus: HttpStatus,
    val message: String = "성공",
    val timeStamp: Instant = Instant.now(),
    val data: T? = null
) {
    companion object {
        fun <T> ok(data: T?): ApiResponse<T> {
            return ApiResponse(data = data, httpStatus = HttpStatus.OK)
        }
        fun ok(code: String, message: String): ApiResponse<String> {
            return ApiResponse(code = code, message = message, httpStatus = HttpStatus.OK)
        }
        fun ok(): ApiResponse<Unit> {
            return ApiResponse(httpStatus = HttpStatus.OK)
        }
        fun fail(): ApiResponse<Unit> {
            return ApiResponse(httpStatus = HttpStatus.INTERNAL_SERVER_ERROR, message = "실패")
        }
        fun created(): ApiResponse<Unit> {
            return ApiResponse(httpStatus = HttpStatus.CREATED)
        }
        fun <T> created(data: T?): ApiResponse<T> {
            return ApiResponse(data = data, httpStatus = HttpStatus.CREATED)
        }
        fun accepted(): ApiResponse<Unit> {
            return ApiResponse(httpStatus = HttpStatus.ACCEPTED)
        }
    }
}