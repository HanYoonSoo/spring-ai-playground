package com.hanyoonsoo.springaiplayground.chat.service

import com.hanyoonsoo.springaiplayground.chat.dto.request.SendChatSimpleQueryRequest
import com.hanyoonsoo.springaiplayground.chat.dto.response.SendChatSimpleQueryResponse
import com.hanyoonsoo.springaiplayground.openai.dto.OpenAiSendRequest
import com.hanyoonsoo.springaiplayground.openai.enum.OpenAiModel
import com.hanyoonsoo.springaiplayground.openai.service.OpenAiService
import org.springframework.stereotype.Service

@Service
class ChatService(
    private val openAiService: OpenAiService,
) {
    fun sendChatSimpleQuery(request: SendChatSimpleQueryRequest): SendChatSimpleQueryResponse {
        val systemMessage = """
        You are a helpful AI assistant.
        
        - Answer in natural Korean text only.
        - Do NOT use JSON format.
        - Do NOT use curly braces `{}`.
        - Respond as plain text that a human can read directly.
        """.trimIndent()

        val userMessage = request.query

        val assistantMessage = """
        사용자의 질문에 대해 명확하고 자연스러운 문장으로 답변해 주세요.
        불필요한 포맷이나 구조화된 출력 없이 설명 위주로 작성하세요.
        """.trimIndent()

        val chatResponse = openAiService.sendChatMessage(
            OpenAiSendRequest(
                systemMessage = systemMessage,
                userMessage = userMessage,
                model = OpenAiModel.GPT_4_1
            )
        )

        return SendChatSimpleQueryResponse(chatResponse ?: "")
    }
}