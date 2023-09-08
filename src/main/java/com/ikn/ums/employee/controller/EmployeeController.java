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

import com.ikn.ums.employee.VO.EmployeeListVO;
import com.ikn.ums.employee.VO.EmployeeVO;
import com.ikn.ums.employee.entity.Employee;
import com.ikn.ums.employee.service.EmployeeService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/employees") 
@Slf4j
public class EmployeeController {
	
	@Autowired
	private EmployeeService employeeService;
	
	/**
	 * save a manually created employee object into UMS DB
	 * @param employee
	 * @return
	 */
	@PostMapping("/save")
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
	
	/**
	 * used while user authenticates into UMS application
	 * @param email
	 * @return employee object.
	 */
	@GetMapping("/{email}")
	public ResponseEntity<?> getUserDetailsWithDepartment(@PathVariable String email){
		try {
			EmployeeVO employeeDto = employeeService.fetchEmployeeDetailsWithDepartment(email);
			System.out.println(employeeDto);
			return new ResponseEntity<>(employeeDto, HttpStatus.OK);
		}catch (Exception e) {
			return new ResponseEntity<>("No user found with provided email "+email, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
	}
	
	/**
	 * can use this method for first time when no user are present in DB.
	 * @return
	 */
	@PostMapping("/save-all")
	public ResponseEntity<?> saveAllAzureUserProfiles(){
		log.info("Entered into saveAllUserProfiles()");
		try {
			int insertedUsersCount =  employeeService.saveAzureUsers();
			return new ResponseEntity<>(insertedUsersCount, HttpStatus.CREATED);
		}catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Users data could not be saved , Please try again",HttpStatus.INTERNAL_SERVER_ERROR);
		}		
	}
	
	/**
	 * save single azure user profile based using this method
	 * @param userPrincipalName
	 * @return
	 */
	@PostMapping("/save/{userPrincipalName}")
	public ResponseEntity<?> saveAzureUserProfile(@PathVariable String userPrincipalName){
		try {
			Integer dbEmployeeCount = employeeService.searchEmployeeByEmail(userPrincipalName);
			if(dbEmployeeCount == 0) {
				String message = employeeService.saveAzureUser(userPrincipalName);
				return new ResponseEntity<>(message, HttpStatus.CREATED);
			}else {
				return new ResponseEntity<>("User already exists in Database, duplicate users not allowed", HttpStatus.NOT_ACCEPTABLE);
			}
		}catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("May be the user does not exist in your azure DB, please try again",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * fetch all employees from UMS DB
	 * this method is used by batch processing microservice
	 * @return
	 */
	@GetMapping("/get-all")
	public ResponseEntity<?> getAllEmployees(){
		List<Employee> employeesDbList = employeeService.findAllEmployees();
		EmployeeListVO empListVO = new EmployeeListVO();
		empListVO.setEmployee(employeesDbList);
		return new ResponseEntity<>(empListVO, HttpStatus.OK);
	}
	
}
