package com.hanyoonsoo.springaiplayground.rag.utils

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.springframework.ai.document.Document
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.stereotype.Component

@Component
object ChunkExtractor {

    fun fromPdf(pdfDocument: PDDocument, projectId: Long, originalFilename: String?, uploadDate: String): Pair<List<Document>, StringBuilder> {
        val pages = mutableListOf<Document>()
        val stripper = PDFTextStripper()
        val totalPages = pdfDocument.numberOfPages
        val fullTextBuilder = StringBuilder()

        for (pageIndex in 0 until totalPages) {
            stripper.startPage = pageIndex + 1
            stripper.endPage = pageIndex + 1

            val pageText = stripper.getText(pdfDocument).trim()
            if (pageText.isNotBlank()) {
                fullTextBuilder.append(pageText).append("\n")

                val metadata = mapOf(
                    "projectId" to projectId, // 프로젝트 ID 추가
                    "fileName" to (originalFilename ?: "unknown"),
                    "uploadDate" to uploadDate,
                    "pageNumber" to (pageIndex + 1),
                    "docType" to "chunk"
                )
                pages.add(Document(pageText, metadata))
            }
        }

        // 1. 원문 청킹
        val splitter = TokenTextSplitter.builder()
            .withChunkSize(512)
            .withMinChunkSizeChars(350)
            .withMinChunkLengthToEmbed(10)
            .withKeepSeparator(true)
            .build()

        return splitter.apply(pages) to fullTextBuilder
    }
}