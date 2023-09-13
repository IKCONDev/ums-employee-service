package com.ikn.ums.employee.service;

import java.util.List;

import com.ikn.ums.employee.VO.EmployeeVO;
import com.ikn.ums.employee.VO.TeamsUserProfileVO;
import com.ikn.ums.employee.entity.Employee;

public interface EmployeeService {
	
	EmployeeVO fetchEmployeeDetailsWithDepartment(String email);
	Employee saveEmployee(Employee employee);
	EmployeeVO getEmployeeWithDepartment(Integer employeeId);
	//save all users by fetching details from organization azure active directory
	Integer saveAzureUsers();
	//save single user from azure
	String saveAzureUser(String azureUserPrincipalName);
	//search employee count by their username
	Integer searchEmployeeByEmail(String email);
	void deleteEmployee(Integer employeeId);
	List<Employee> getAllEmployees();

}
