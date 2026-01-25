package com.hanyoonsoo.springaiplayground.rag.dto.response

data class SendChatResponse(
    val answer: String,
    val documents: List<DocumentOutput>
)
