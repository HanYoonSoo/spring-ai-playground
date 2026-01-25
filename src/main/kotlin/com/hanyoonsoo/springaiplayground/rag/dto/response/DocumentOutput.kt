package com.hanyoonsoo.springaiplayground.rag.dto.response

data class DocumentOutput(
    val content: String,
    val fileName: String,
    val pageNumber: Int?,
    val docType: String
)
