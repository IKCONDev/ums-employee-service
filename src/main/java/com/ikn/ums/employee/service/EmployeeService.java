package com.ikn.ums.employee.service;

import java.util.List;

import com.ikn.ums.employee.VO.EmployeeVO;
import com.ikn.ums.employee.VO.TeamsUserProfileVO;
import com.ikn.ums.employee.entity.Employee;

public interface EmployeeService {
	
	EmployeeVO fetchEmployeeDetailsWithDepartment(String email);
	Employee saveEmployee(Employee employee);
	EmployeeVO getUserWithDepartment(Integer employeeId);
	EmployeeVO getAllEmployeesWithDepartment();
	//saves users data by fetching user data from microsoft azure
	Integer saveAllEmployeesFromAzure(List<TeamsUserProfileVO> teamsUserProfilesList);
	List<Employee> findAllEmployees();

}
