package com.hanyoonsoo.springaiplayground.openai.service

import com.hanyoonsoo.springaiplayground.openai.dto.OpenAiSendRequest
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.stereotype.Service

@Service
class OpenAiService(
    private val openAiChatClient: ChatClient,
) {
    fun sendChatMessage(
        request: OpenAiSendRequest,
    ): String? {
        with (request) {
            val systemMessage = systemMessage?.let {
                SystemMessage(systemMessage)
            }

            val userMessage = userMessage?.let {
                UserMessage(userMessage)
            }

            val assistantMessage = assistantMessage?.let {
                AssistantMessage(assistantMessage)
            }

            val openAiChatOptions = OpenAiChatOptions.builder()
                .model(model.modelName)
                .temperature(0.7)
                .build()

            val prompt = Prompt(listOf(systemMessage, userMessage, assistantMessage), openAiChatOptions)

            return openAiChatClient.prompt(prompt)
                .call()
                .content()
        }
    }
}