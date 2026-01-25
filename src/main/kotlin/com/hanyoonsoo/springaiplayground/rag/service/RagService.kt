package com.hanyoonsoo.springaiplayground.rag.service

import com.hanyoonsoo.springaiplayground.openai.dto.OpenAiSendRequest
import com.hanyoonsoo.springaiplayground.openai.enum.OpenAiModel
//import com.hanyoonsoo.springaiplayground.openai.enum.OpenAiTool
import com.hanyoonsoo.springaiplayground.openai.service.OpenAiService
import com.hanyoonsoo.springaiplayground.rag.dto.response.SendChatResponse
import com.hanyoonsoo.springaiplayground.rag.dto.response.DocumentOutput
import com.hanyoonsoo.springaiplayground.rag.dto.request.SendChatRequest
import com.hanyoonsoo.springaiplayground.rag.prompt.RagPrompt
import com.hanyoonsoo.springaiplayground.rag.repository.DocumentVectorStoreRepository
import com.hanyoonsoo.springaiplayground.rag.utils.ChunkExtractor
import org.apache.pdfbox.pdmodel.PDDocument
import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class RagService(
    private val documentVectorStoreRepository: DocumentVectorStoreRepository,
    private val openAiService: OpenAiService,
    private val chunkExtractor: ChunkExtractor
) {
    private val log = org.slf4j.LoggerFactory.getLogger(this::class.java)

    fun addDocuments(projectId: Long, file: MultipartFile) {
        val pdfDocument = PDDocument.load(file.inputStream)
        val uploadDate = java.time.LocalDateTime.now().toString()

        try {
            val (chunks, fullTextBuilder) = chunkExtractor.fromPdf(
                pdfDocument,
                projectId,
                file.originalFilename,
                uploadDate
            )

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

    fun chatWithRag(projectId: Long, request: SendChatRequest): SendChatResponse {
        val documents = findDocuments(projectId, request.query)

        val context = if (documents.isNotEmpty()) {
            val joined = documents.joinToString("\n\n") { doc -> "- ${doc.text}" }
            joined
        } else {
            log.warn("No documents found for ProjectId: {} and Query: {}", projectId, request.query)
            "No related documents found in the database."
        }

        // 3. OpenAI 프롬프트 생성 (Tool 사용 가능하도록 설정)
        val openAiRequest = OpenAiSendRequest(
            systemMessage = RagPrompt.RAG_SYSTEM.template,
            userMessage = RagPrompt.RAG_USER.format(context, request.query),
            model = OpenAiModel.GPT_4O, // 고성능 모델
//            tools = listOf(OpenAiTool.TAVILY_SEARCH) // Tavily 검색 도구 활성화
        )

        val answer = openAiService.sendChatMessage(openAiRequest) ?: "답변 생성 실패"
        
        val documentResponses = documents.map { doc ->
            DocumentOutput(
                content = doc.text ?: "",
                fileName = doc.metadata["fileName"] as? String ?: "unknown",
                pageNumber = (doc.metadata["pageNumber"] as? Int) ?: (doc.metadata["pageNumber"] as? String)?.toIntOrNull(),
                docType = doc.metadata["docType"] as? String ?: "unknown"
            )
        }

        return SendChatResponse(answer, documentResponses)
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
//            tools = listOf(OpenAiTool.TAVILY_SEARCH)TAVILY_SEARCH
        )
        
        return openAiService.sendChatMessage(request) ?: "요약 실패"
    }
}

