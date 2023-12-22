package com.example.finalExam.controller;

import com.example.finalExam.model.Employee;
import com.example.finalExam.repository.EmployeeProjectRepository;
import com.example.finalExam.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
public class EmployeeController {

    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    EmployeeProjectRepository employeeProjectRepository;

    @GetMapping("/viewAll")
    public String listEmployees(Model model) {
        List<Employee> employees = employeeRepository.findAll();
        model.addAttribute("employees", employees);
        return "employee";
    }

    @GetMapping("/add")
    public String addEmployeeForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "AddEmployee";
    }

    @PostMapping("/add")
    public String addEmployee(@ModelAttribute Employee employee, Model model) {
        if (employeeRepository.existsByEmpId(employee.getEmpId())) {
            model.addAttribute("errorMessage", "Employee with the same empId already exists");
            return "AddEmployee";
        }
        employeeRepository.save(employee);
        return "redirect:/viewAll";
    }

    @GetMapping("/edit/{id}")
    public String editEmployeeForm(@PathVariable Long id, Model model) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee Id:" + id));
        model.addAttribute("employee", employee);
        return "EditEmployee";
    }

    @PostMapping("/edit/{id}")
    public String editEmployee(@PathVariable Long id, @ModelAttribute Employee employee, RedirectAttributes redirectAttributes) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("Invalid employee Id:" + id));
        // Check if the employee is referenced in EmployeeProject
        boolean isReferenced = employeeProjectRepository.existsByEmployee(existingEmployee);

        if (isReferenced) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot edit employee. It is referenced in EmployeeProject.");
        } else {
            existingEmployee.setEmpId(employee.getEmpId());
            employeeRepository.save(existingEmployee);
        }
        return "redirect:/viewAll";
    }

    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            employeeRepository.deleteById(id);
            return "redirect:/viewAll";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unable to delete the employee.");
            return "redirect:/viewAll";
        }
    }
}
