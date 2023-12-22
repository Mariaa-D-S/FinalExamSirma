package com.example.finalExam.repository;

import com.example.finalExam.model.Employee;
import com.example.finalExam.model.EmployeeProject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeProjectRepository extends JpaRepository<EmployeeProject, Long> {
    boolean existsByEmployee_EmpIdAndProject_ProjectId(Long empId, Long projectId);
    boolean existsByEmployee(Employee employee);
}
