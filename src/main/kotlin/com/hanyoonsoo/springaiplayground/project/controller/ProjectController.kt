package com.hanyoonsoo.springaiplayground.project.controller

import com.hanyoonsoo.springaiplayground.global.common.response.ApiResponse
import com.hanyoonsoo.springaiplayground.project.dto.request.CreateProjectRequest
import com.hanyoonsoo.springaiplayground.project.dto.response.GetProjectsResponse
import com.hanyoonsoo.springaiplayground.project.service.ProjectService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/projects")
class ProjectController(
    private val projectService: ProjectService
) {
    @PostMapping()
    fun createProject(
        @RequestBody request: CreateProjectRequest
    ): ApiResponse<Unit> {
        projectService.createProject(request)
        return ApiResponse.created()
    }

    @GetMapping()
    fun getProjects(): ApiResponse<GetProjectsResponse> {
        return ApiResponse.ok(projectService.getProjects())
    }
}