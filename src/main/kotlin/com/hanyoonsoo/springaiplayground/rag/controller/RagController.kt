package com.hanyoonsoo.springaiplayground.rag.controller

import com.hanyoonsoo.springaiplayground.global.common.response.ApiResponse
import com.hanyoonsoo.springaiplayground.rag.dto.SendChatRequest
import com.hanyoonsoo.springaiplayground.rag.service.RagService
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/rag")
class RagController(
    private val ragService: RagService
) {

    @PostMapping("/projects/{projectId}/documents")
    fun addDocuments(
        @PathVariable projectId: Long,
        @RequestPart("file") file: MultipartFile
    ): ApiResponse<Unit> {
        ragService.addDocuments(projectId, file)
        return ApiResponse.created()
    }

    @GetMapping("/projects/{projectId}/search")
    fun search(
        @PathVariable projectId: Long,
        @RequestParam("query") query: String
    ): ApiResponse<List<String>> {
        val results = ragService.searchSimilarDocuments(projectId, query)
        return ApiResponse.ok(results)
    }

    @PostMapping("/projects/{projectId}/chat")
    fun chat(
        @PathVariable projectId: Long,
        @RequestBody request: SendChatRequest
    ): ApiResponse<String> {
        val answer = ragService.chatWithRag(projectId, request)
        return ApiResponse.ok(answer)
    }
}
