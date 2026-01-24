package com.hanyoonsoo.springaiplayground.chat.dto.request

import jakarta.validation.constraints.NotBlank

data class SendChatSimpleQueryRequest(
    @field:NotBlank
    val query: String,
)
