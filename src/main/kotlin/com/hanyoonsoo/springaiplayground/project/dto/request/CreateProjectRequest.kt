package com.hanyoonsoo.springaiplayground.project.dto.request

import jakarta.validation.constraints.NotBlank

data class CreateProjectRequest(
    @field:NotBlank
    val projectName: String,
)
