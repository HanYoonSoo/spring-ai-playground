package com.hanyoonsoo.springaiplayground.global.config

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenAiChatClientConfig {
    @Bean
    fun openAiChatClient(openAiChatModel: OpenAiChatModel): ChatClient {
        return ChatClient.create(openAiChatModel)
    }
}