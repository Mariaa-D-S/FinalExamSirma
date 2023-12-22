package com.example.finalExam.service;

import com.example.finalExam.model.Employee;
import com.example.finalExam.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {
    EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> allEmployees(){
        return employeeRepository.findAll();
    }

    public Employee add(Employee employee){
        if (employeeRepository.existsByEmpId(employee.getEmpId())) {
            throw new IllegalArgumentException("Employee with ID " + employee.getEmpId() + " already exists");
        }
        return employeeRepository.save(employee);
    }

    public Employee editById(Employee updatedEmployee, Long id){
        return employeeRepository.findById(id)
                .map(employee -> {
                    employee.setEmpId(updatedEmployee.getEmpId());
                    // Set other fields as needed
                    return employeeRepository.save(employee);
                })
                .orElseThrow(() -> new IllegalArgumentException("Invalid Id"));
    }

    public void delete(Long id){
        boolean exists = employeeRepository.existsById(id);
        if(exists) {
            employeeRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Invalid Id");
        }
    }
}
