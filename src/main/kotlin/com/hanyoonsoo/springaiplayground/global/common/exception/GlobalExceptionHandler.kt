package com.hanyoonsoo.springaiplayground.global.common.exception

import com.hanyoonsoo.springaiplayground.global.common.response.ApiResponse
import com.hanyoonsoo.springaiplayground.openai.enum.OpenAiModel
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleException(
        response: HttpServletResponse,
        e: Exception
    ): ApiResponse<Unit> {
        response.status = HttpStatus.INTERNAL_SERVER_ERROR.value()

        return ApiResponse.fail()
    }
}