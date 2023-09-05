package com.ikn.ums.employee.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ikn.ums.employee.VO.EmployeeVO;
import com.ikn.ums.employee.VO.TeamsUserProfileVO;
import com.ikn.ums.employee.dto.EmployeeDto;
import com.ikn.ums.employee.entity.Employee;
import com.ikn.ums.employee.service.EmployeeService;
import com.ikn.ums.employee.service.EmployeeServiceImpl;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/employees") 
@Slf4j
public class EmployeeController {
	
	@Autowired
	private EmployeeService employeeService;
	
	@PostMapping("/")
	public Employee saveEmployee(@RequestBody Employee employee) {
		log.info("EmployeeController.saveEmployee() ENTERED");
		return employeeService.saveEmployee(employee);
	}
	
	/*
	@GetMapping("/{id}")
	public ResponseTemplateVO getUserWithDepartment (@PathVariable("id") Integer employeeId) {
		System.out.println("EmployeeController.getUserWithDepartment() : employeeId : " + employeeId);
		log.info("EmployeeController.getUserWithDepartment() ENTERED");
		return employeeService.getUserWithDepartment(employeeId);
	}
	*/
	
	//used while user authenticates into UMS application
	@GetMapping("/{email}")
	public ResponseEntity<EmployeeVO> getUserDetailsWithDepartment(@PathVariable String email){
		EmployeeVO employeeDto = employeeService.fetchEmployeeDetailsWithDepartment(email);
		System.out.println("EmployeeController.getUserDetailsWithDepartment() ENTERED ");
		System.out.println("************ employeeDto :" + employeeDto);
		
		log.info("EmployeeController.getUserDetailsWithDepartment() ENTERED ");
		
		return new ResponseEntity<>(employeeDto, HttpStatus.OK);
	}
	
	@PostMapping("/save")
	public ResponseEntity<?> saveAllUserProfiles(@RequestBody List<TeamsUserProfileVO> teamsUserProfilesList){
		try {
			int insertedUsersCount =  employeeService.saveAllEmployeesFromAzure(teamsUserProfilesList);
			return new ResponseEntity<>(insertedUsersCount, HttpStatus.CREATED);
		}catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Users data could not be saved , Please try again",HttpStatus.INTERNAL_SERVER_ERROR);
		}		
	}
	
	@GetMapping("/")
	public ResponseEntity<List<Employee>> getAllEmployees(){
		List<Employee> employeesDbList = employeeService.findAllEmployees();
		return new ResponseEntity<>(employeesDbList, HttpStatus.OK);
	}
	
}
