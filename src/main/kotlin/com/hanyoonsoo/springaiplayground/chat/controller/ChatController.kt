package com.hanyoonsoo.springaiplayground.chat.controller

import com.hanyoonsoo.springaiplayground.chat.dto.request.SendChatSimpleQueryRequest
import com.hanyoonsoo.springaiplayground.chat.dto.response.SendChatSimpleQueryResponse
import com.hanyoonsoo.springaiplayground.chat.service.ChatService
import com.hanyoonsoo.springaiplayground.global.common.response.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/chat")
@Tag(name = "Chat API", description = "Chat API")
class ChatController(
    private val chatService: ChatService
) {
    @PostMapping("/simple-query")
    fun sendChatSimpleQuery(
        @Valid @RequestBody request: SendChatSimpleQueryRequest
    ): ApiResponse<SendChatSimpleQueryResponse> {
        return ApiResponse.ok(chatService.sendChatSimpleQuery(request))
    }
}