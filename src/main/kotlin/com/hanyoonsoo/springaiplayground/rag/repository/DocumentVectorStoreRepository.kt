package com.hanyoonsoo.springaiplayground.rag.repository

import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.stereotype.Repository

@Repository
class DocumentVectorStoreRepository(
    private val documentVectorStore: VectorStore,
) {
    fun saveDocuments(documents: List<Document>) {
        documentVectorStore.add(documents)
    }

    fun findDocumentsSimilaritySearch(searchRequest: SearchRequest): List<Document>? {
        return documentVectorStore.similaritySearch(searchRequest)
    }
}