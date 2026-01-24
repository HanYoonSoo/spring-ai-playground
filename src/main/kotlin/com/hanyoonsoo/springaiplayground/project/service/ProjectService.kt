package com.hanyoonsoo.springaiplayground.project.service

import com.hanyoonsoo.springaiplayground.project.dto.request.CreateProjectRequest
import com.hanyoonsoo.springaiplayground.project.dto.response.GetProjectsResponse
import com.hanyoonsoo.springaiplayground.project.dto.response.ProjectOutput
import com.hanyoonsoo.springaiplayground.project.entity.Project
import com.hanyoonsoo.springaiplayground.project.repository.ProjectRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectService(
    private val projectRepository: ProjectRepository
) {
    @Transactional
    fun createProject(request: CreateProjectRequest) {
        projectRepository.save(Project.from(request.projectName))
    }

    @Transactional(readOnly = true)
    fun getProjects(): GetProjectsResponse {
        return GetProjectsResponse(projectRepository.findAll().map(ProjectOutput::from))
    }
}