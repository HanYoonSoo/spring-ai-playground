package com.hanyoonsoo.springaiplayground.rag.entity

import jakarta.persistence.*
import org.hibernate.annotations.Array
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.UUID

/**
 * Spring AI의 VectorStore에 의해 관리되는 테이블입니다.
 * 이 엔티티는 JPA를 통한 데이터 조작을 위한 것이 아니라,
 * 테이블 스키마 파악 및 문서화를 돕기 위한 용도입니다.
 * 
 * 실제 데이터 저장 및 검색은 RagService 및 VectorStore를 통해 이루어집니다.
 */
@Entity
@Table(name = "document_vector_store")
class DocumentVectorStore(
    @Id
    @Column(name = "id")
    val id: UUID? = null,

    @Column(name = "content")
    val content: String? = null,

    @Column(name = "metadata")
    @JdbcTypeCode(SqlTypes.JSON)
    val metadata: Map<String, Any>? = null,

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = 1536)
    @Column(name = "embedding", columnDefinition = "vector(1536)")
    val embedding: FloatArray? = null
) {
    // 기본 생성자 (JPA 필수)
    protected constructor() : this(null, null, null, null)
}
