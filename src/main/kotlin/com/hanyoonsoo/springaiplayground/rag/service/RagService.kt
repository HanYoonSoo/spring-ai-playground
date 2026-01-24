package com.hanyoonsoo.springaiplayground.rag.service

import com.hanyoonsoo.springaiplayground.openai.dto.OpenAiSendRequest
import com.hanyoonsoo.springaiplayground.openai.enum.OpenAiModel
import com.hanyoonsoo.springaiplayground.openai.enum.OpenAiTool
import com.hanyoonsoo.springaiplayground.openai.service.OpenAiService
import com.hanyoonsoo.springaiplayground.rag.dto.SendChatRequest
import com.hanyoonsoo.springaiplayground.rag.prompt.RagPrompt
import com.hanyoonsoo.springaiplayground.rag.repository.DocumentVectorStoreRepository
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.springframework.ai.document.Document
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class RagService(
    private val documentVectorStoreRepository: DocumentVectorStoreRepository,
    private val openAiService: OpenAiService
) {

    fun addDocuments(projectId: Long, file: MultipartFile) {
        val pdfDocument = PDDocument.load(file.inputStream)

        try {
            val pages = mutableListOf<Document>()
            val stripper = PDFTextStripper()
            val totalPages = pdfDocument.numberOfPages
            val uploadDate = java.time.LocalDateTime.now().toString()
            val fullTextBuilder = StringBuilder()

            for (pageIndex in 0 until totalPages) {
                stripper.startPage = pageIndex + 1
                stripper.endPage = pageIndex + 1

                val pageText = stripper.getText(pdfDocument).trim()
                if (pageText.isNotBlank()) {
                    fullTextBuilder.append(pageText).append("\n")

                    val metadata = mapOf(
                        "projectId" to projectId, // 프로젝트 ID 추가
                        "fileName" to (file.originalFilename ?: "unknown"),
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

            val chunks = splitter.apply(pages)
            documentVectorStoreRepository.saveDocuments(chunks)

            // 2. 문서 요약 및 임베딩 (Summary Embedding)
            val fullText = fullTextBuilder.toString()
            if (fullText.isNotBlank()) {
                val summary = summarizeContent(fullText)

                val summaryMetadata = mapOf(
                    "projectId" to projectId,
                    "fileName" to (file.originalFilename ?: "unknown"),
                    "uploadDate" to uploadDate,
                    "docType" to "summary"
                )

                val summaryDoc = Document(summary, summaryMetadata)
                documentVectorStoreRepository.saveDocuments(listOf(summaryDoc))
            }

        } finally {
            pdfDocument.close()
        }
    }

    fun searchSimilarDocuments(projectId: Long, query: String): List<String> {
        val documents = findDocuments(projectId, query)

        return documents.map { doc ->
            val type = doc.metadata["docType"] ?: "unknown"
            val page = doc.metadata["pageNumber"] ?: "ALL"
            val file = doc.metadata["fileName"] ?: "?"

            if (type == "summary") {
                "[SUMMARY] [File: $file] ${doc.text}"
            } else {
                "[CHUNK] [File: $file, Page: $page] ${doc.text}"
            }
        }
    }

    fun chatWithRag(projectId: Long, request: SendChatRequest): String {
        val documents = findDocuments(projectId, request.query)

        val context = if (documents.isNotEmpty()) {
            documents.joinToString("\n\n") { doc -> "- ${doc.text}" }
        } else {
            "No related documents found in the database."
        }

        // 3. OpenAI 프롬프트 생성 (Tool 사용 가능하도록 설정)
        val openAiRequest = OpenAiSendRequest(
            systemMessage = RagPrompt.RAG_SYSTEM.template,
            userMessage = RagPrompt.RAG_USER.format(context, request.query),
            model = OpenAiModel.GPT_4O, // 고성능 모델
            tools = listOf(OpenAiTool.TAVILY_SEARCH) // Tavily 검색 도구 활성화
        )

        return openAiService.sendChatMessage(openAiRequest) ?: "답변 생성 실패"
    }

    private fun findDocuments(projectId: Long, query: String): List<Document> {
        val filterExpression = "projectId == $projectId"

        val searchRequest = SearchRequest.builder()
            .query(query)
            .topK(5)
            .similarityThreshold(0.5)
            .filterExpression(filterExpression)
            .build()
        
        return documentVectorStoreRepository.findDocumentsSimilaritySearch(searchRequest) ?: emptyList()
    }

    private fun summarizeContent(content: String): String {
        val safeContent = if (content.length > 10000) content.substring(0, 10000) else content

        val request = OpenAiSendRequest(
            systemMessage = RagPrompt.DOCUMENT_SUMMARY_SYSTEM.template,
            userMessage = RagPrompt.DOCUMENT_SUMMARY_USER.format(safeContent),
            model = OpenAiModel.GPT_4O_MINI,
            tools = listOf(OpenAiTool.TAVILY_SEARCH)
        )
        
        return openAiService.sendChatMessage(request) ?: "요약 실패"
    }
}

