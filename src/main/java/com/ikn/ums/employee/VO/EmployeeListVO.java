package com.ikn.ums.employee.VO;

import java.util.List;

import com.ikn.ums.employee.entity.Employee;

import lombok.Data;

@Data
public class EmployeeListVO {
	
	private List<Employee> employee;
	
}
