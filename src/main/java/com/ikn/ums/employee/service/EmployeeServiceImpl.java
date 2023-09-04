package com.ikn.ums.employee.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ikn.ums.employee.VO.DepartmentVO;
import com.ikn.ums.employee.VO.EmployeeVO;
import com.ikn.ums.employee.VO.TeamsUserProfileVO;
import com.ikn.ums.employee.entity.Employee;
import com.ikn.ums.employee.repository.EmployeeRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ModelMapper mapper;

	public EmployeeServiceImpl() {
		// mapper = new ModelMapper();
		// mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
	}

	@Override
	public Employee saveEmployee(Employee employee) {
		log.info("EmployeeService.saveEmployee() ENTERED");
		return employeeRepository.save(employee);
	}

	// fetch user details based on username (email)
	@Override
	public EmployeeVO fetchEmployeeDetailsWithDepartment(String email) {

		EmployeeVO employeeVO = null;
		Optional<Employee> optEmployee = employeeRepository.findByEmail(email);
		if (optEmployee.isPresent()) {
			employeeVO = new EmployeeVO();
			Employee employee = optEmployee.get();

			// get department details of employee from departments microservice
			DepartmentVO department = restTemplate.getForObject(
					"http://UMS-DEPARTMENT-SERVICE/departments/" + employee.getDepartmentId(), DepartmentVO.class);
			// set department to employee
			// map entity to VO
			mapper.map(employee, employeeVO);
			// set department to employee
			employeeVO.setDepartment(department);
		}
		return employeeVO;
	}

	public EmployeeVO getUserWithDepartment(Integer employeeId) {

		System.out.println("EmployeeService.getUserWithDepartment() : employeeId : " + employeeId);

		log.info("EmployeeService.getUserWithDepartment() ENTERED");
		// ResponseTemplateVO responseTemplateVO = new ResponseTemplateVO();
		EmployeeVO employeeVO = new EmployeeVO();
		Optional<Employee> optEmployee = employeeRepository.findById(employeeId);
		Employee employee = optEmployee.get();
		System.out.println("EmployeeService.getUserWithDepartment() : employee.getDepartmentId() : 0 "
				+ employee.getDepartmentId());
//			Department department = restTemplate.getForObject("http://localhost:9001/departments/" + employee.getDepartmentId(), Department.class);
		/**
		 * There might be multiple instances running over multiple hosts and different
		 * ports. To achieve this, we use the MS application name instead of hard code
		 * as above. Let the Department Service running anywhere, based on the below
		 * configuration, it get the service from the Service Registry.
		 * 
		 */
		DepartmentVO department = restTemplate.getForObject(
				"http://UMS-DEPARTMENT-SERVICE/departments/" + employee.getDepartmentId(), DepartmentVO.class);
		System.out.println(employee);
		// map entity to VO
		mapper.map(employee, employeeVO);
		// set department to employee
		employeeVO.setDepartment(department);
		System.out.println(employeeVO);
		return employeeVO;
	}
	
	

	@Override
	public EmployeeVO getAllEmployeesWithDepartment() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer saveAllEmployeesFromAzure(List<TeamsUserProfileVO> teamsUserProfilesList) {
		List<Employee> employeesList = new ArrayList<>();
		teamsUserProfilesList.forEach(profile -> {
			Employee e = new Employee();
			//id will be auto generated
			e.setFirstName(profile.getGivenName());
			e.setLastName(profile.getSurname());
			e.setTeamsUserId(profile.getUserId());
			e.setEmail(profile.getMail());
			e.setDesignation(profile.getJobTitle());
			e.setTwoFactorAuthentication(false);
			//setting default password, //Test@123
			e.setEncryptedPassword("$2a$10$054UvQ85YjjEMnb2Okh9r.qJNDOE9trkRhEjeNE6tdPeeBJNEHZpa");
			e.setOtpCode(0);
			e.setDepartmentId(1L);
			//set default role
			e.setUserRole("Team Member");
			employeesList.add(e);
		});
		List<Employee> dbEmployees = employeeRepository.saveAll(employeesList);
		return dbEmployees.size();
	}

	@Override
	public List<Employee> findAllEmployees() {
		return employeeRepository.findAll();
	}
}
