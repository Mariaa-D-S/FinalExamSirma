package com.example.finalExam.reader;

import com.example.finalExam.model.Employee;
import com.example.finalExam.model.EmployeeProject;
import com.example.finalExam.model.Project;
import com.example.finalExam.repository.EmployeeProjectRepository;
import com.example.finalExam.repository.EmployeeRepository;
import com.example.finalExam.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReadFromCSV {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    EmployeeProjectRepository employeeProjectRepository;


    @Autowired
    public ReadFromCSV() {
    }

    public void readDataFromCSV(String fileName) {
        List<EmployeeProject> employeeProjects = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            reader.readLine();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                if (parts.length >= 4) {
                    Long empId = Long.parseLong(parts[0]);
                    Long projectId = Long.parseLong(parts[1]);
                    if (!employeeProjectRepository.existsByEmployee_EmpIdAndProject_ProjectId(empId, projectId)) {
                        String dateFromStr = parts[2];
                        if (dateFromStr.endsWith(",")) {
                            dateFromStr = dateFromStr.substring(0, dateFromStr.length() - 1);
                        }
                        LocalDate dateFrom = LocalDate.parse(dateFromStr, dateFormatter);

                        String dateToStr = parts[3];
                        LocalDate dateTo = null;

                        if (!dateToStr.isEmpty() && !dateToStr.equals("NULL")) {
                            if (dateToStr.endsWith(",")) {
                                dateToStr = dateToStr.substring(0, dateToStr.length() - 1);
                            }
                            dateTo = LocalDate.parse(dateToStr, dateFormatter);
                        }

                        // Check if the Employee exists
                        Employee employee = employeeRepository.findByEmpId(empId);
                        if (employee == null) {
                            employee = new Employee();
                            employee.setEmpId(empId);
                            employeeRepository.save(employee);
                        }

                        // Check if the Project exists
                        Project project = projectRepository.findByProjectId(projectId);
                        if (project == null) {
                            project = new Project();
                            project.setProjectId(projectId);
                            projectRepository.save(project);
                        }

                        EmployeeProject employeeProject = new EmployeeProject(employee, project, dateFrom, dateTo);
                        employeeProjects.add(employeeProject);
                    }
                }
            }

            employeeProjectRepository.saveAll(employeeProjects);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
