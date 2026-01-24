package com.hanyoonsoo.springaiplayground.project.dto.response

import com.hanyoonsoo.springaiplayground.project.entity.Project

data class GetProjectsResponse(
    val projects: List<ProjectOutput>
)

data class ProjectOutput(
    val id: Long,
    val projectName: String,
) {
    companion object {
        fun from(project: Project) = ProjectOutput(project.id, project.name)
    }
}