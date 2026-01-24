package com.hanyoonsoo.springaiplayground.global.common.response

import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

@RestControllerAdvice
class ApiResponseAdvice: ResponseBodyAdvice<Any> {

    override fun supports(
        returnType: MethodParameter,
        converterType: Class<out HttpMessageConverter<*>?>
    ): Boolean {
        return returnType.parameterType != ResponseEntity::class.java
    }

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>?>,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): Any? {
        // Swagger 관련 경로는 ApiResponse로 래핑 X
        val path: String = request.uri.path

        if (isSwaggerPath(path)) {
            return body
        }

        if (body is ApiResponse<*>) {
            response.setStatusCode(HttpStatusCode.valueOf(body.httpStatus.value()))
            return body
        }

        return ApiResponse.ok(body)
    }

    private fun isSwaggerPath(path: String): Boolean {
        return path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") ||
                path.startsWith("/health")
    }
}