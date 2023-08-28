package com.ikn.ums.employee.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ikn.ums.employee.VO.Department;
import com.ikn.ums.employee.VO.ResponseTemplateVO;
import com.ikn.ums.employee.entity.Employee;
import com.ikn.ums.employee.repository.EmployeeRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private RestTemplate restTemplate;
	
	public Employee saveEmployee(Employee employee) {
		log.info("EmployeeService.saveEmployee() ENTERED" );
		return employeeRepository.save(employee);
	}

	public ResponseTemplateVO getUserWithDepartment(Long employeeId) {
		
		System.out.println("EmployeeService.getUserWithDepartment() : employeeId : "+ employeeId ); 
		
			log.info("EmployeeService.getUserWithDepartment() ENTERED");
			ResponseTemplateVO responseTemplateVO = new ResponseTemplateVO();
			Employee employee = employeeRepository.findByEmployeeId(employeeId);
			
			System.out.println("EmployeeService.getUserWithDepartment() : employee.getDepartmentId() : 0 " + employee.getDepartmentId());
//			Department department = restTemplate.getForObject("http://localhost:9001/departments/" + employee.getDepartmentId(), Department.class);
			/**
			 * There might be multiple instances running over multiple hosts and different ports. To achieve this, we use the MS application name instead of hard code as above.
			 * Let the Department Service running anywhere, based on the below configuration, it get the service from the Service Registry.
			 * 
			 */
			Department department =
						restTemplate.getForObject("http://UMS-DEPARTMENT-SERVICE/departments/" + employee.getDepartmentId()
										,Department.class);

			responseTemplateVO.setEmployee(employee);
			responseTemplateVO.setDepartment(department);
			
			return responseTemplateVO;
		}
}
