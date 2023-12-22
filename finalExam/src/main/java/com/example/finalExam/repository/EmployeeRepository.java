package com.example.finalExam.repository;

import com.example.finalExam.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Employee findByEmpId(Long id);
    boolean existsByEmpId(Long empId);

}
