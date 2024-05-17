package com.ikn.ums.employee.service;

import java.util.List;

import com.ikn.ums.employee.VO.EmployeeVO;
import com.ikn.ums.employee.VO.TeamsUserProfileVO;
import com.ikn.ums.employee.dto.EmployeeDto;
import com.ikn.ums.employee.entity.Employee;

public interface EmployeeService {
	
	EmployeeVO fetchEmployeeDetailsWithDepartment(String email);
	EmployeeDto saveEmployee(EmployeeDto employee);
	EmployeeDto updateEmployee(EmployeeDto employee);
	void deleteEmployee(Integer employeeId);
	EmployeeVO getEmployeeWithDepartment(Integer employeeId);
	//get all UMS employee profiles
	List<Employee> getAllEmployees();
	//save all users by fetching details from organization azure active directory
	Integer saveAzureUsers();
	//save single user from azure
	String saveAzureUser(String azureUserPrincipalName);
	//search employee count by their username
   //Integer searchEmployeeByEmail(String email);
	boolean checkIfEmployeeExists(String employeeEmailId);
	boolean deleteAllEmployeesById(List<Integer> employeeIds);
	List<Employee> getEmployeeReporteesData(String emailId);
	List<Employee> getAllEmployeesWithUserStatus(boolean isUser);
	void updateEmployeeStatus(String email);
	void updateEmployeeStatustoFalse(String email);
	List<EmployeeDto> getAllEmployeesByEmailIds(List<String> emailIds);
	boolean getEmployeesByEmployeeOrgId(String employeeOrgId);
	List<Employee> getAllSubordinateOfEmployee(String emailId);
	List<Employee> getActiveEmployees();
	TeamsUserProfileVO getAzureOrganizationalUser(String emailId);
	List<EmployeeDto> getEmployeesOfDepartment(Long depatrmentId, boolean isUser);
	
}
