package com.hanyoonsoo.springaiplayground.project.repository

import com.hanyoonsoo.springaiplayground.project.entity.Project
import org.springframework.data.jpa.repository.JpaRepository

interface ProjectRepository: JpaRepository<Project, Long>{
}