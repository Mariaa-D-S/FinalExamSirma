package com.example.finalExam.controller;

import com.example.finalExam.reader.ReadFromCSV;
import com.example.finalExam.service.EmployeeProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class HomeController {

    private final ReadFromCSV reader;

    @Autowired
    EmployeeProjectService employeeProjectService;

    @Autowired
    public HomeController(ReadFromCSV reader) {
        this.reader = reader;
    }

    @GetMapping("/")
    public String importCsv() {
        reader.readDataFromCSV("src/main/resources/employees.csv");
        return "index";
    }

    @GetMapping("/mostWorkingPair")
    public String visualizeData(Model model) {
        Map<String, Object> visualizationData = employeeProjectService.calculateWorkingDays();
        model.addAttribute("visualizationData", visualizationData);
        return "mostWorkingPairInfo";
    }
}
