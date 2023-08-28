package com.ikn.ums.employee.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ikn.ums.employee.VO.ResponseTemplateVO;
import com.ikn.ums.employee.entity.Employee;
import com.ikn.ums.employee.service.EmployeeService;

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
	
	@GetMapping("/{id}")
	public ResponseTemplateVO getUserWithDepartment (@PathVariable("id") Long employeeId) {
		System.out.println("EmployeeController.getUserWithDepartment() : employeeId : " + employeeId);
		log.info("EmployeeController.getUserWithDepartment() ENTERED");
		return employeeService.getUserWithDepartment (employeeId);
	}
}
