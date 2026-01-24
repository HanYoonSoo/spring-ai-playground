package com.hanyoonsoo.springaiplayground.global.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun springOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Spring AI PlayGround")
                    .version("1.0")
                    .description("Spring AI Playground")
            )
    }
}