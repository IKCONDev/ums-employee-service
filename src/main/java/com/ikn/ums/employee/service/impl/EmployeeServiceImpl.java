package com.ikn.ums.employee.service.impl;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.azure.core.credential.AccessToken;
import com.ikn.ums.employee.VO.DepartmentVO;
import com.ikn.ums.employee.VO.EmployeeVO;
import com.ikn.ums.employee.VO.TeamsUserProfileVO;
import com.ikn.ums.employee.entity.Employee;
import com.ikn.ums.employee.exception.BusinessException;
import com.ikn.ums.employee.exception.EmployeeExistsException;
import com.ikn.ums.employee.exception.EmptyInputException;
import com.ikn.ums.employee.exception.EmptyListException;
import com.ikn.ums.employee.exception.EntityNotFoundException;
import com.ikn.ums.employee.exception.ErrorCodeMessages;
import com.ikn.ums.employee.model.UserProfilesResponseWrapper;
import com.ikn.ums.employee.repository.EmployeeRepository;
import com.ikn.ums.employee.service.EmployeeService;
import com.ikn.ums.employee.utils.InitializeMicrosoftGraph;

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

	private String accessToken = null;

	private AccessToken acToken = new AccessToken(this.accessToken, OffsetDateTime.now());

	@Autowired
	private InitializeMicrosoftGraph microsoftGraph;

	public EmployeeServiceImpl() {
		// mapper = new ModelMapper();
		// mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
	}

//	@Override
//	public Employee saveEmployee(Employee employee) {
//		log.info("EmployeeService.saveEmployee() ENTERED");
//		return employeeRepository.save(employee);
//	}

	@Override
	public Employee saveEmployee(Employee employee) {
		log.info("EmployeeService.saveEmployee() ENTERED : " + employee);
		if (employee == null) {
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_EMP_ENTITY_IS_NULL_CODE,
					ErrorCodeMessages.ERR_EMP_ENTITY_IS_NULL_MSG);
		}
		Employee savedEmployee = null;
		if (checkIfEmployeeExists(employee.getEmail()) == false) {
			log.info("EmployeeService.saveEmployee() Saving Employee....");
			LocalDateTime date = LocalDateTime.now();
			employee.setCreatedDateTime(date);
			employee.setUser(false);
			savedEmployee = employeeRepository.save(employee);
		} else {
			log.info("EmployeeService.saveEmployee() Employee Already Exists !");
			throw new EmployeeExistsException(ErrorCodeMessages.ERR_EMP_EXISTS_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_EMP_EXISTS_EXCEPTION_MSG);
		}
		return savedEmployee;
	}

	@Override
	public Employee updateEmployee(Employee employee) {
		log.info("EmployeeService.updateEmployee() ENTERED : " + employee);
		if (employee == null) {
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_EMP_ENTITY_IS_NULL_CODE,
					ErrorCodeMessages.ERR_EMP_ENTITY_IS_NULL_MSG);
		}
		Employee updatedEmployee = null;
		Employee dbEmployee = null;
		Optional<Employee> optEmployee = employeeRepository.findById(employee.getId());
		if(optEmployee.isPresent()) {
				dbEmployee = optEmployee.get();	
		}
		log.info("EmployeeService.updateEmployee() Updating Employee....");
		dbEmployee.setEmail(employee.getEmail());
		dbEmployee.setFirstName(employee.getFirstName());
		dbEmployee.setLastName(employee.getLastName());
		dbEmployee.setReportingManager(employee.getReportingManager());
		dbEmployee.setDepartmentId(employee.getDepartmentId());
		dbEmployee.setEmpDesignation(employee.getEmpDesignation());
		dbEmployee.setModifiedDateTime(LocalDateTime.now());
		dbEmployee.setModifiedByEmailId(employee.getEmail());
		dbEmployee.setModifiedBy(employee.getFirstName());
		dbEmployee.setTeamsUserId(employee.getTeamsUserId());
		dbEmployee.setGender(employee.getGender());
		dbEmployee.setDateOfJoining(employee.getDateOfJoining());
		dbEmployee.setEmployeeOrgId(employee.getEmployeeOrgId());
		updatedEmployee = employeeRepository.save(dbEmployee);
		return updatedEmployee; 
	}

	@Override
	public void deleteEmployee(Integer employeeId) {
		log.info("EmployeeServiceImpl.deleteEmployee() ENTERED : employeeId : " + employeeId);
		if (employeeId == 0)
			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_ID_NOT_FOUND_CODE,
					ErrorCodeMessages.ERR_EMP_ID_NOT_FOUND_MSG);

		Optional<Employee> dbEmployee = null;
		dbEmployee = employeeRepository.findById(employeeId);
		if (dbEmployee.isPresent()) {
			log.info("EmployeeService.deleteEmployee() under execution....");
			employeeRepository.deleteById(employeeId);
		} else {
			log.info("EmployeeService.saveEmployee() Employee Already Exists !");
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_EMP_ENTITY_IS_NULL_CODE,
					ErrorCodeMessages.ERR_EMP_ENTITY_IS_NULL_MSG);
		}
	}

	// fetch user details based on username (email)
	@Override
	public EmployeeVO fetchEmployeeDetailsWithDepartment(String email) {
		
		System.out.println("EmployeeServiceImpl.fetchEmployeeDetailsWithDepartment() ENTEREED : email :" + email);

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

	/**
	 * Retrieve the Employee Details based on the Employee ID
	 * 
	 * @param Integer employeeId
	 * @return EmployeeVO
	 */
	public EmployeeVO getEmployeeWithDepartment(Integer employeeId) {
		log.info("EmployeeService.getEmployeeWithDepartment() ENTERED : employeeId : " + employeeId);
		if (employeeId < 0) {
			log.info("EmployeeServiceImpl.getEmployeeWithDepartment() in employee id is null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_ID_NOT_FOUND_CODE,
					ErrorCodeMessages.ERR_EMP_ID_NOT_FOUND_MSG);
		}
		// ResponseTemplateVO responseTemplateVO = new ResponseTemplateVO();
		EmployeeVO employeeVO = new EmployeeVO();
		Optional<Employee> optEmployee = employeeRepository.findById(employeeId);
		if (optEmployee.isEmpty()) {
			log.info("EmployeeServiceImpl.getEmployeeWithDepartment() in optEmployee :: employee id is null");
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_EMP_ENTITY_IS_NULL_CODE,
					ErrorCodeMessages.ERR_EMP_ENTITY_IS_NULL_MSG);
		}
		Employee employee = optEmployee.get();
		log.info("EmployeeServiceImpl.getEmployeeWithDepartment() : employee.getDepartmentId() : " + employee.getDepartmentId());
		log.info("Retrieved Employee Object. Passing the department id from employee to get the department details of the employee....");
		DepartmentVO department = null;
//				Department department = restTemplate.getForObject("http://localhost:9001/departments/" + employee.getDepartmentId(), Department.class);
		/**
		 * There might be multiple instances running over multiple hosts and different
		 * ports. To achieve this, we use the MS application name instead of hard code
		 * as above. Let the Department Service running anywhere, based on the below
		 * configuration, it get the service from the Service Registry.
		 * 
		 */
		try {
			log.info("Calling Department Microservice !");
				department = restTemplate.getForObject(
					"http://UMS-DEPARTMENT-SERVICE/departments/" + employee.getDepartmentId(), DepartmentVO.class);
		} catch (RestClientException restClientException) {
			log.info("Exception Occured while calling Department Microservice !");
			throw new BusinessException(ErrorCodeMessages.ERR_EMP_DEPT_REST_CLIENT_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_EMP_DEPT_REST_CLIENT_EXCEPTION_MSG);
		}
		//System.out.println(employee);
		// map entity to VO
		mapper.map(employee, employeeVO);
		// set department to employee
		employeeVO.setDepartment(department);
		//System.out.println(employeeVO);
		return employeeVO;
	}

//	@Override
//	public List<Employee> findAllEmployees() {
//		List<Employee> employeeList = employeeRepository.findAll();
//		return employeeList;
//	}

	@Override
	public List<Employee> getAllEmployees() {
		List<Employee> employeesList = null;
		employeesList = employeeRepository.findAll();
		if (employeesList == null || employeesList.isEmpty())
			throw new EmptyListException(ErrorCodeMessages.ERR_EMP_LIST_IS_EMPTY_CODE,
					ErrorCodeMessages.ERR_EMP_LIST_IS_EMPTY_MSG);
		return employeesList;
	}

	@Transactional
	@SuppressWarnings("rawtypes")
	@Override
	public Integer saveAzureUsers() {
		// get access token from MS teams server , only if it is already null
		if (this.acToken.isExpired()) {
			log.info("Access Token expired");
			this.acToken = this.microsoftGraph.initializeMicrosoftGraph();
			log.info("Access Token Refreshed");
			this.accessToken = this.acToken.getToken();
		}

		// get users from azure active directory
		String userProfileUrl = "https://graph.microsoft.com/v1.0/users?$filter=accountEnabled eq true and userType eq 'Member'";

		// prepare headers
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", "Bearer " + this.accessToken);
		httpHeaders.add("content-type", "application/json");

		// prepare http entity with headers
		HttpEntity httpEntity = new HttpEntity<>(httpHeaders);

		// prepare the rest template and hit the graph api user end point
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<UserProfilesResponseWrapper> userProfilesResponse = restTemplate.exchange(userProfileUrl,
				HttpMethod.GET, httpEntity, UserProfilesResponseWrapper.class);

		// get all user profiles from reponse object
		List<TeamsUserProfileVO> teamsUserProfilesList = userProfilesResponse.getBody().getValue();

		List<Employee> employeesList = new ArrayList<>();
		teamsUserProfilesList.forEach(profile -> {
			Employee e = new Employee();
			// id will be auto generated
			e.setFirstName(profile.getDisplayName());
			e.setLastName(profile.getSurname());
			e.setTeamsUserId(profile.getUserId());
			e.setEmail(profile.getUserPrincipalName());
			e.setDesignation(profile.getJobTitle());
			// e.setTwoFactorAuthentication(false);
			// setting default password, //Test@123 in encrypted format
			// e.setEncryptedPassword("$2a$10$054UvQ85YjjEMnb2Okh9r.qJNDOE9trkRhEjeNE6tdPeeBJNEHZpa");
			// e.setOtpCode(0);
			e.setDepartmentId(1L);
			// set default role
			// e.setUserRole("Team Member");
			employeesList.add(e);
		});
		List<Employee> dbEmployees = employeeRepository.saveAll(employeesList);
		return dbEmployees.size();
	}

	/**
	 * converts a azure user profile to UMS employee profile and saves in UMS DB.
	 */
	@Override
	public String saveAzureUser(String azureUserPrincipalName) {
		Employee e = null;
		TeamsUserProfileVO profile = getAzureUser(azureUserPrincipalName);
		Employee insertedUser = null;

		// check for null and convert azure user profile to UMS employee profile
		if (profile != null) {
			e = new Employee();
			// id will be auto generated
			e.setFirstName(profile.getDisplayName());
			e.setLastName(profile.getSurname());
			e.setTeamsUserId(profile.getUserId());
			e.setEmail(profile.getUserPrincipalName());
			e.setDesignation(profile.getJobTitle());
			// e.setTwoFactorAuthentication(false);
			// setting default password, //Test@123 in encrypted format
			// e.setEncryptedPassword("$2a$10$054UvQ85YjjEMnb2Okh9r.qJNDOE9trkRhEjeNE6tdPeeBJNEHZpa");
			// e.setOtpCode(0);
			e.setDepartmentId(1L);
			// set default role
			// e.setUserRole("Team Member");

			// save user
			insertedUser = employeeRepository.save(e);
		}
		return "User " + insertedUser.getEmail() + " saved successfully";
	}

	/**
	 * get a single user profile from azure active directory based on the user
	 * principal name
	 * 
	 * @param userPrincipalName
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private TeamsUserProfileVO getAzureUser(String userPrincipalName) {

		// get access token from MS teams server , only if it is already null
		if (this.acToken.isExpired()) {
			log.info("Access Token expired");
			this.acToken = this.microsoftGraph.initializeMicrosoftGraph();
			log.info("Access Token Refreshed");
			this.accessToken = this.acToken.getToken();
		}

		// get users
		String userProfileUrl = "https://graph.microsoft.com/v1.0/users/" + userPrincipalName;

		// prepare headers
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", "Bearer " + this.accessToken);
		httpHeaders.add("content-type", "application/json");

		// prepare http entity with headers
		HttpEntity httpEntity = new HttpEntity<>(httpHeaders);

		// prepare the rest template and hit the graph api user end point
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<TeamsUserProfileVO> userProfileResponse = restTemplate.exchange(userProfileUrl, HttpMethod.GET,
				httpEntity, TeamsUserProfileVO.class);

		// get all user profiles from reponse object
		TeamsUserProfileVO userDto = userProfileResponse.getBody();

		return userDto;

	}

//	@Override
//	public Integer searchEmployeeByEmail(String email) {
//		log.info("EmployeeServiceImpl.searchEmployeeByEmail() ENTERED : email : " + email);
//		if (email == null || email.isEmpty() )
//			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_EMAIL_ID_NOT_FOUND_CODE,
//					ErrorCodeMessages.ERR_EMP_EMAIL_ID_NOT_FOUND_MSG);
//		Integer count = employeeRepository.searchEmployeeDetailsByMail(email);
//		return count;
//	}

	public boolean checkIfEmployeeExists(String employeeEmailId) {
		log.info("EmployeeServiceImpl.checkIfEmployeeExists() ENTERED : employeeEmailId : " + employeeEmailId);
		boolean checkifEmployeeExists = false;
		if (employeeEmailId == null || employeeEmailId.isEmpty() || employeeEmailId.length() == 0)
			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_EMAIL_ID_NOT_FOUND_CODE,
					ErrorCodeMessages.ERR_EMP_EMAIL_ID_NOT_FOUND_MSG);
		Integer count = employeeRepository.checkIfEmployeeExists(employeeEmailId);
		if (count > 0)
			checkifEmployeeExists = true;
		return checkifEmployeeExists;
	}

	@Override
	public boolean deleteAllEmployeesById(List<Integer> employeeIds) {
		log.info("EmployeeServiceImpl.deleteAllEmployeesById() ENTERED : employeeIds : ");
		boolean isDeleted = false;
	    employeeRepository.deleteAllById(employeeIds);
		// TODO Auto-generated method stub
		isDeleted = true;
		return isDeleted;
	}

	@Override
	public List<Employee> getEmployeeReporteesData(String emailId) {
		if(emailId == "" || emailId == "null" || emailId == null) {
			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_EMAIL_ID_NOT_FOUND_CODE, 
					ErrorCodeMessages.ERR_EMP_EMAIL_ID_NOT_FOUND_MSG);
		}
		List<Employee> employeeReporteesList = employeeRepository.findEmployeeReportees(emailId);
		return employeeReporteesList;
	}
	
	@Override
	public List<Employee> getAllEmployeesWithUserStatus() {
		List<Employee> employeesList = null;
		boolean userStatus = false;
		employeesList = employeeRepository.findAllEmployeesWithUserStatus(userStatus);
		if (employeesList == null || employeesList.isEmpty())
			throw new EmptyListException(ErrorCodeMessages.ERR_EMP_LIST_IS_EMPTY_CODE,
					ErrorCodeMessages.ERR_EMP_LIST_IS_EMPTY_MSG);
		return employeesList;
	}
	
	@Transactional
	@Override
	public void updateEmployeeStatus(String email) {
		System.out.println("updateEmployeeStatus() is entered");
		Employee dbUser = null;
		Optional<Employee> optEmployee = employeeRepository.findByEmail(email);
		if(optEmployee.isPresent()) {
			dbUser = optEmployee.get();
		}
		dbUser.setUser(true);
	}
	
	@Transactional
	@Override
	public void updateEmployeeStatustoFalse(String email) {
		System.out.println("updateEmployeeStatus() is entered");
		Employee dbUser = null;
		Optional<Employee> optEmployee = employeeRepository.findByEmail(email);
		if(optEmployee.isPresent()) {
			dbUser = optEmployee.get();
		}
		dbUser.setUser(false);
	}
	
	
	

}
