package com.ikn.ums.employee.VO;

import com.ikn.ums.employee.entity.Employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTemplateVO {
	
	private Employee employee;
	private Department department;	
	
}
