package com.hanyoonsoo.springaiplayground.openai.dto

import com.hanyoonsoo.springaiplayground.openai.enum.OpenAiModel

data class OpenAiSendRequest(
    val systemMessage: String? = "",
    val userMessage: String? = "",
    val assistantMessage: String? = "",
    val model: OpenAiModel
)