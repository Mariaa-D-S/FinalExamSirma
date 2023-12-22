# FinalExamSirma

This project contains crud operations for employee and angorithm for calculating the most working pair.It uses mvc structure, reader for CSV file and database to store all the data. When the CSV file is read the data is saved in database in three tables: Employee, Project and EmployeeProject.Each row from file is actually EmployeeProject object. The reader get the employee id and project id to create Employee and Project objects, if they does not exist. On home page there are links for the add employee form, view all employee and the most working pair. If you click the view all employee link, you will see all the employee list and also you can delete and edit them if the employee is not conected to project, because if you edit/delete already conected to project employee, it is edited/deleted only in database, so the data between file and database is will be not consistent. And on the next read it will create the eployee with the old id, so in this way we have two rows from the same employee. You can add employee with id which does not exist already. But if you want to add EmployeeProject you have to write it in the CSV file. The algorithum for calculating the most working pair is presented in the following methods: 

getSortedCoupleKey 

This method ensures a consistent order for the employee pair key. Compares employee IDs and orders them to create a consistent key. 

splitByProjectId 

This method organizes EmployeeProject entities into a map based on project ID. 

calculateDaysWorkedTogether 

This method calculates the days worked together between two date ranges. If one of the dateTo values is null, sets it to the current date. Uses the Duration.between method to calculate the days worked between the start and end dates.

calculateWorkingDaysForProject

This method calculates the days worked together for employee pairs in a specific project. Creates a set (processedCouples) to keep track of processed employee pairs to avoid duplicate calculations. Nested iteration over the employeesInProject list to consider all possible pairs of employees. Checks if both employees are part of the same project. If not, skips to the next iteration. Calls getSortedCoupleKey to get a consistent order for the employee pair key. Checks if the employee pair has already been processed in this project to avoid duplicate calculations. Calls calculateDaysWorkedTogether to calculate the days worked together for the employee pair. Populates the workingDaysMap with the calculated days worked for the employee pair in the specific project. 

calculateTotalDaysWorked 

This method aggregates the days worked together for each employee pair across different projects. Creates a map (coupleTotalDaysMap) where the key is the employee pair key, and the value is the total days worked together. Nested iteration over the workingDaysMap to aggregate days worked together for each employee pair across different projects. Returns the coupleTotalDaysMap with the days worked for each employee pair. 

calculateProjectDays 

This method organizes the days worked for each project and employee pair. Creates a map (projectDaysMap) where the key is the employee pair key, and the value is another map with project IDs and days worked. Nested iteration over the workingDaysMap to organize the days worked for each project and employee pair. Returns the projectDaysMap with the days worked for each project and employee pair. I use this method as a helper for vuzualization. 

prepareVisualizationData 

This method prepares the data for visualization. Calls calculateTotalDaysWorked to get the total days worked for each employee pair. Finds the employee pair that worked the most days together. Calls calculateProjectDays to get the days worked for each project and employee pair. Formats the data into a structure suitable for visualization. 

calculateWorkingDays 

This method calculates the total days worked together for each employee pair across different projects. Retrieves all EmployeeProject entities from the employeeProjectRepository. Calls splitByProjectId to organize the EmployeeProject entities into a map (projectsMap) where the key is the project ID, and the value is a list of EmployeeProject entities associated with that project. Calls calculateWorkingDaysForProject for each list of EmployeeProject entities in a project. Populates the workingDaysMap where the key is the project ID, and the value is a map containing employee pair keys and the corresponding days worked together. Calls prepareVisualizationData with the workingDaysMap to format the data for visualization. 
