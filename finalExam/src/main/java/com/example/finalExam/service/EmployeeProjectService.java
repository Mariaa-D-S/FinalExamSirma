package com.example.finalExam.service;

import com.example.finalExam.model.EmployeeProject;
import com.example.finalExam.repository.EmployeeProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Service
public class EmployeeProjectService {
    @Autowired
    EmployeeProjectRepository employeeProjectRepository;

    public List<EmployeeProject> allEmployeeProjects(){
        return employeeProjectRepository.findAll();
    }

    public Map<String, Object> calculateWorkingDays() {
        List<EmployeeProject> employeeProjects = allEmployeeProjects();

        Map<Long, List<EmployeeProject>> projectsMap = splitByProjectId(employeeProjects);
        Map<Long, Map<String, Integer>> workingDaysMap = new HashMap<>();

        for (List<EmployeeProject> employeesInProject : projectsMap.values()) {
            calculateWorkingDaysForProject(employeesInProject, workingDaysMap);
        }

        return prepareVisualizationData(workingDaysMap);
    }

    private Map<String, Integer> calculateTotalDaysWorked(Map<Long, Map<String, Integer>> workingDaysMap) {
        Map<String, Integer> coupleTotalDaysMap = new HashMap<>();

        workingDaysMap.forEach((projectId, projectMap) -> {
            projectMap.forEach((coupleKey, daysWorked) -> {
                //ignore project ID
                String[] coupleParts = coupleKey.split("_");
                String coupleWithoutProject = coupleParts[1];

                coupleTotalDaysMap.merge(coupleWithoutProject, daysWorked, Integer::sum);
            });
        });

        return coupleTotalDaysMap;
    }

    private Map<String, Map<String, Integer>> calculateProjectDays(Map<Long, Map<String, Integer>> workingDaysMap) {
        Map<String, Map<String, Integer>> projectDaysMap = new HashMap<>();

        workingDaysMap.forEach((projectId, projectMap) -> {
            projectMap.forEach((coupleKey, daysWorked) -> {
                // Extract employee IDs and project ID from the couple key
                String[] coupleParts = coupleKey.split("_");
                String coupleWithoutProject = coupleParts[1];
                String project_Id = coupleParts[0];

                projectDaysMap.computeIfAbsent(coupleWithoutProject, k -> new HashMap<>())
                        .put(project_Id, daysWorked);
            });
        });

        return projectDaysMap;
    }

    private void calculateWorkingDaysForProject(List<EmployeeProject> employeesInProject, Map<Long, Map<String, Integer>> workingDaysMap) {

        Set<String> processedCouples = new HashSet<>();

        for (int i = 0; i < employeesInProject.size(); i++) {
            for (int j = i + 1; j < employeesInProject.size(); j++) {
                EmployeeProject employeeProject1 = employeesInProject.get(i);
                EmployeeProject employeeProject2 = employeesInProject.get(j);

                if (employeeProject1.getProject().equals(employeeProject2.getProject())) {
                    String coupleKey = getSortedCoupleKey(
                            employeeProject1.getProject().getProjectId(),
                            employeeProject1.getEmployee().getEmpId(),
                            employeeProject2.getEmployee().getEmpId());

                    if (processedCouples.contains(coupleKey)) {
                        continue;
                    }

                    int daysWorked = calculateDaysWorkedTogether(
                            employeeProject1.getDateFrom(),
                            employeeProject1.getDateTo(),
                            employeeProject2.getDateFrom(),
                            employeeProject2.getDateTo());

                    workingDaysMap
                            .computeIfAbsent(employeeProject1.getProject().getProjectId(), k -> new HashMap<>())
                            .put(coupleKey, daysWorked);

                    processedCouples.add(coupleKey);
                }
            }
        }
    }

    private Map<String, Object> prepareVisualizationData(Map<Long, Map<String, Integer>> workingDaysMap) {
        Map<String, Object> visualizationData = new LinkedHashMap<>();
        Map<String, Integer> coupleTotalDaysMap = calculateTotalDaysWorked(workingDaysMap);

        Map.Entry<String, Integer> mostWorkedPairEntry = coupleTotalDaysMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        if (mostWorkedPairEntry != null) {
            String mostWorkedPair = mostWorkedPairEntry.getKey();
            int totalDaysWorked = mostWorkedPairEntry.getValue();

            visualizationData.put("Employees", mostWorkedPair);
            visualizationData.put("TotalDays", totalDaysWorked);

            // Prepare data for each project worked by the couple
            List<Map<String, Object>> projectsList = new ArrayList<>();
            Map<String, Map<String, Integer>> projectDaysMap = calculateProjectDays(workingDaysMap);
            projectDaysMap.forEach((coupleKey, projectMap) -> {
                if (coupleKey.equals(mostWorkedPair)) {
                    projectMap.forEach((project_Id, daysWorked) -> {
                        Map<String, Object> projectData = new LinkedHashMap<>();
                        projectData.put("ProjectID", Long.parseLong(project_Id));
                        projectData.put("Days", daysWorked);

                        projectsList.add(projectData);
                    });
                }
            });

            visualizationData.put("Projects", projectsList);
        }

        return visualizationData;
    }

    private Map<Long, List<EmployeeProject>> splitByProjectId(List<EmployeeProject> employeeProjects) {
        Map<Long, List<EmployeeProject>> projectsMap = new HashMap<>();
        for (EmployeeProject employeeProject : employeeProjects) {
            Long projectId = employeeProject.getProject().getProjectId();
            projectsMap.computeIfAbsent(projectId, k -> new ArrayList<>()).add(employeeProject);
        }
        return projectsMap;
    }

    private int calculateDaysWorkedTogether(LocalDate dateFrom1, LocalDate dateTo1, LocalDate dateFrom2, LocalDate dateTo2) {
        // Check which date range starts later and ends earlier
        LocalDate startDate = (dateFrom1.isAfter(dateFrom2)) ? dateFrom1 : dateFrom2;

        if(dateTo1 == null){
            dateTo1 = LocalDate.now();
        } else if (dateTo2 == null) {
            dateTo2 = LocalDate.now();
        }
        LocalDate endDate = dateTo1.isBefore(dateTo2) ? dateTo1 : dateTo2;

        long daysWorked = Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay()).toDays();
        return Math.toIntExact(Math.max(0, daysWorked));
    }

    private String getSortedCoupleKey(Long projectId, Long empId1, Long empId2) {
        // Ensure consistent order for the couple keys, considering project ID
        return projectId + "_" +
                ((empId1.compareTo(empId2) < 0)
                        ? empId1 + ", " + empId2
                        : empId2 + ", " + empId1);
    }
}
