package com.hanyoonsoo.springaiplayground.global.config

import org.springframework.ai.openai.OpenAiEmbeddingModel
import org.springframework.ai.vectorstore.pgvector.PgVectorStore
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate

@Configuration
class VectorStoreConfig {

    @Bean
    fun documentVectorStore(
        jdbcTemplate: JdbcTemplate,
        embeddingModel: OpenAiEmbeddingModel
    ): PgVectorStore {
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
            .vectorTableName("document_vector_store")
            .indexType(PgVectorStore.PgIndexType.HNSW)
            .initializeSchema(false)
            .build()
    }
}