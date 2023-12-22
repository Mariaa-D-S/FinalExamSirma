package com.example.finalExam.repository;

import com.example.finalExam.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project,Long> {
    Project findByProjectId(Long id);
}
