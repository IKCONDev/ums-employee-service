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
import com.ikn.ums.employee.dto.EmployeeDto;
import com.ikn.ums.employee.entity.Employee;
import com.ikn.ums.employee.exception.BusinessException;
import com.ikn.ums.employee.exception.EmployeeExistsException;
import com.ikn.ums.employee.exception.EmployeeIdExistsException;
import com.ikn.ums.employee.exception.EmptyInputException;
import com.ikn.ums.employee.exception.EmptyListException;
import com.ikn.ums.employee.exception.EntityNotFoundException;
import com.ikn.ums.employee.exception.ErrorCodeMessages;
import com.ikn.ums.employee.model.UserProfilesResponseWrapper;
import com.ikn.ums.employee.repository.EmployeeRepository;
import com.ikn.ums.employee.service.EmployeeService;
import com.ikn.ums.employee.utils.AdminConstants;
import com.ikn.ums.employee.utils.InitializeMicrosoftGraph;
import com.netflix.servo.util.Strings;

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
	
	private String departmentMicroservicerURL = "http://UMS-DEPARTMENT-SERVICE/departments/";
	
	private String usersMicroservicerURL = "http://UMS-USERS-SERVICE/user/";

	@Autowired
	private InitializeMicrosoftGraph microsoftGraph;

	public EmployeeServiceImpl() {
		// mapper = new ModelMapper();
		// mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
	}

	@Override
	public EmployeeDto saveEmployee(EmployeeDto employee) {
		log.info("saveEmployee() ENTERED : " + employee);
		if (employee == null) {
			log.info("saveEmployee() EntityNotFoundException : employee object is null");
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_EMP_ENTITY_IS_NULL_CODE,
					ErrorCodeMessages.ERR_EMP_ENTITY_IS_NULL_MSG);
		}
		Integer count = employeeRepository.isEmployeeIdExists(employee.getEmployeeOrgId());
		if(count > 0) {
			log.info("saveEmployee() EmployeeIdExistsException : Employee Id already exists : "+employee.getEmployeeOrgId());
			throw new EmployeeIdExistsException(ErrorCodeMessages.ERR_EMP_EMPORGID_EXISTS_CODE, 
					ErrorCodeMessages.ERR_EMP_EMPORGID_EXISTS_MSG);
		}
		Employee savedEmployee = null;
		EmployeeDto savedEmployeeDto = new EmployeeDto();
		log.info("saveEmployee() is under execution...");
		if (checkIfEmployeeExists(employee.getEmail()) == false) {
			log.info("saveEmployee() Saving Employee....");
			LocalDateTime date = LocalDateTime.now();
			employee.setCreatedDateTime(date);
			employee.setUser(false);
		    employee.setEmployeeStatus(AdminConstants.STATUS_ACTIVE);
		    employee.setBatchProcessStatus("Enabled");
			Employee entity = new Employee();
			mapper.map(employee, entity);
			savedEmployee = employeeRepository.save(entity);
			mapper.map(savedEmployee, savedEmployeeDto);
			
		} else {
			log.info("saveEmployee() Employee Already Exists !");
			throw new EmployeeExistsException(ErrorCodeMessages.ERR_EMP_EXISTS_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_EMP_EXISTS_EXCEPTION_MSG);
		}
		log.info("saveEmployee() executed successfully");
		return savedEmployeeDto;
	}

	@Override
	public EmployeeDto updateEmployee(EmployeeDto employee) {
		log.info("EmployeeService.updateEmployee() ENTERED : " + employee);
		if (employee == null) {
			log.info("updateEmployee() EntityNotFoundException : employee object is null");
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_EMP_ENTITY_IS_NULL_CODE,
					ErrorCodeMessages.ERR_EMP_ENTITY_IS_NULL_MSG);
		}
		log.info("updateEmployee() is under execution...");
		Employee updatedEmployee = null;
		Optional<Employee> optEmployee = employeeRepository.findById(employee.getId());
		if(!optEmployee.isPresent()) {
			log.info("updateEmployee() EntityNotFoundException : employee object is null");
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_EMP_ENTITY_NOT_FOUND_CODE,
					ErrorCodeMessages.ERR_EMP_ENTITY_NOT_FOUND_MSG);
		}
		if(optEmployee.isEmpty()) {
			log.info("updateEmployee() EntityNotFoundException : DB employee is null with provided Id : "+employee.getEmployeeOrgId());
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_EMP_DBENTITY_NOT_FOUND_CODE,
					ErrorCodeMessages.ERR_EMP_DBENTITY_NOT_FOUND_MSG);
		}
		if(!employee.getEmployeeOrgId().equals(optEmployee.get().getEmployeeOrgId())) {
			Integer count = employeeRepository.isEmployeeIdExists(employee.getEmployeeOrgId());
			if(count > 0) {
				log.info("updateEmployee() EmployeeIdExistsException : Employee Id already exists : "+employee.getId());
				throw new EmployeeIdExistsException(ErrorCodeMessages.ERR_EMP_EMPORGID_EXISTS_CODE, 
						ErrorCodeMessages.ERR_EMP_EMPORGID_EXISTS_MSG);
			}
		}
		//disable batch process , if employee is inactive
		if(!employee.getEmployeeStatus().equalsIgnoreCase("Active")) {
			employee.setBatchProcessStatus("Disabled");
		}
		if(employee.isEnableBatchProcessing()) {
	    	employee.setBatchProcessStatus("Enabled");
	    }else {
	    	employee.setBatchProcessStatus("Disabled");
	    }
		Employee e  = new Employee();
		mapper.map(employee, e);
		updatedEmployee = employeeRepository.save(e);
		EmployeeDto employeeDto = new EmployeeDto();
		System.out.println("employee Status"+ updatedEmployee.getEmployeeStatus());
		if(updatedEmployee.getEmployeeStatus().equals("Inactive")) {
			log.info("calling the user microservice to change the status to Inactive");
			restTemplate.exchange(this.usersMicroservicerURL+"/set-status/"+updatedEmployee.getEmail(),HttpMethod.PUT, null, boolean.class);
		}
		mapper.map(updatedEmployee,employeeDto);
		
		log.info("updateEmployee() executed successfully");
		return employeeDto; 
	}

	@Transactional
	@Override
	public void deleteEmployee(Integer employeeId) {
		log.info("EmployeeServiceImpl.deleteEmployee() ENTERED : employeeId : " + employeeId);
		if (employeeId == 0 || employeeId < 0) {
			log.info("deleteEmployee() EmptyInputException : employee Id  is null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_ID_NOT_FOUND_CODE,
					ErrorCodeMessages.ERR_EMP_ID_NOT_FOUND_MSG);
     	}
		log.info("deleteEmployee() under execution....");
		Optional<Employee> dbEmployee = null;
		dbEmployee = employeeRepository.findById(employeeId);
		if (dbEmployee.isPresent()) {
			//employeeRepository.deleteById(employeeId);
			dbEmployee.get().setEmployeeStatus(AdminConstants.STATUS_IN_ACTIVE);
			restTemplate.exchange(this.usersMicroservicerURL+"/set-status/"+dbEmployee.get().getEmail(),HttpMethod.PUT, null, boolean.class);
			log.info("deleteEmployee() executed successfully");
		} else {
			log.info("An Exception occured while fetching the employee :");
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_EMP_ENTITY_IS_NULL_CODE,
					ErrorCodeMessages.ERR_EMP_ENTITY_IS_NULL_MSG);
		}
	}

	// fetch user details based on username (email)
	@Override
	public EmployeeVO fetchEmployeeDetailsWithDepartment(String email) {
		if (Strings.isNullOrEmpty(email)) {
			log.info("fetchEmployeeDetailsWithDepartment() EmptyInputException : employee email  is null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_EMAIL_ID_NOT_FOUND_CODE,
					ErrorCodeMessages.ERR_EMP_EMAIL_ID_NOT_FOUND_MSG);
		}
			
		log.info("fetchEmployeeDetailsWithDepartment() entered with args email Id - "+ email);
		EmployeeVO employeeVO = null;
		log.info("fetchEmployeeDetailsWithDepartment() is under execution...");
		Optional<Employee> optEmployee = employeeRepository.findByEmail(email);
		if (optEmployee.isPresent()) {
			employeeVO = new EmployeeVO();
			Employee employee = optEmployee.get();

			// get department details of employee from departments microservice
			log.info("calling to Department Microservice");
			DepartmentVO department = restTemplate.getForObject(
					this.departmentMicroservicerURL+ employee.getDepartmentId(), DepartmentVO.class);
			// set department to employee
			// map entity to VO
			if(employee.getBatchProcessStatus().equalsIgnoreCase("Enabled")) {
				employee.setEnableBatchProcessing(true);
			}else {
				employee.setEnableBatchProcessing(false);
			}
			mapper.map(employee, employeeVO);
			// set department to employee
			employeeVO.setDepartment(department);
		}
		log.info("fetchEmployeeDetailsWithDepartment() executed successfully");
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
		log.info("getEmployeeWithDepartment() is under execution...");
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
					this.departmentMicroservicerURL + employee.getDepartmentId(), DepartmentVO.class);
		} catch (RestClientException restClientException) {
			log.info("Exception Occured while calling Department Microservice !");
			throw new BusinessException(ErrorCodeMessages.ERR_EMP_DEPT_REST_CLIENT_EXCEPTION_CODE,
					ErrorCodeMessages.ERR_EMP_DEPT_REST_CLIENT_EXCEPTION_MSG);
		}
		if(employee.getBatchProcessStatus().equalsIgnoreCase("Enabled")) {
			employee.setEnableBatchProcessing(true);
		}else {
			employee.setEnableBatchProcessing(false);
		}
		// map entity to VO
		mapper.map(employee, employeeVO);
		// set department to employee
		employeeVO.setDepartment(department);
		log.info("getEmployeeWithDepartment() executed successfully");
		return employeeVO;
	}

	@Override
	public List<Employee> getAllEmployees() {
		log.info("getAllEmployees() entered");
		List<Employee> employeesList = null;
		log.info("getAllEmployees() is under execution...");
		employeesList = employeeRepository.findAll();
		if (employeesList == null || employeesList.isEmpty())
			throw new EmptyListException(ErrorCodeMessages.ERR_EMP_LIST_IS_EMPTY_CODE,
					ErrorCodeMessages.ERR_EMP_LIST_IS_EMPTY_MSG);
		employeesList.forEach(employee -> {
			if(employee.getBatchProcessStatus().equalsIgnoreCase("Enabled")) {
				employee.setEnableBatchProcessing(true);
			}else {
				employee.setEnableBatchProcessing(false);
			}
		});
		log.info("getAllEmployees() executed successfully");
		return employeesList;
	}

	@Transactional
	@SuppressWarnings("rawtypes")
	@Override
	public Integer saveAzureUsers() {
		// get access token from MS teams server , only if it is already null
		log.info("saveAzureUsers() is entered");
		if (this.acToken.isExpired()) {
			log.info("Access Token expired");
			this.acToken = this.microsoftGraph.initializeMicrosoftGraph();
			log.info("Access Token Refreshed");
			this.accessToken = this.acToken.getToken();
		}
        log.info("saveAzureUsers() is under execution...");
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
			e.setFirstName(profile.getDisplayName());
			e.setLastName(profile.getSurname());
			var lowerCaseEmail = profile.getUserId().toLowerCase();
			e.setTeamsUserId(lowerCaseEmail);
			e.setEmail(profile.getUserPrincipalName());
			e.setDesignation(profile.getJobTitle());
			e.setDepartmentId(1L);
			employeesList.add(e);
		});
		List<Employee> dbEmployees = employeeRepository.saveAll(employeesList);
		log.info("saveAzureUsers() executed successfully");
		return dbEmployees.size();
	}

	/**
	 * converts a azure user profile to UMS employee profile and saves in UMS DB.
	 */
	@Override
	public String saveAzureUser(String azureUserPrincipalName) {
		log.info("saveAzureUser() is entered");
		Employee e = null;
		log.info("saveAzureUser() is under execution...");
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
			e.setDepartmentId(1L);
			insertedUser = employeeRepository.save(e);
		}
		log.info("saveAzureUser() executed successfully");
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
        log.info("getAzureUser() is entered with args : userPrincipalName - "+ userPrincipalName);
		// get access token from MS teams server , only if it is already null
		if (this.acToken.isExpired()) {
			log.info("Access Token expired");
			this.acToken = this.microsoftGraph.initializeMicrosoftGraph();
			log.info("Access Token Refreshed");
			this.accessToken = this.acToken.getToken();
		}
        log.info("getAzureUser() is under execution...");
        log.info("calling to microsoft Azure to fetch the user  profile");
		// get users
		var userProfileUrl = "https://graph.microsoft.com/v1.0/users/" + userPrincipalName;

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
        log.info("getAzureUser() executed successfully");
		return userDto;

	}

	public boolean checkIfEmployeeExists(String employeeEmailId) {
		log.info("checkIfEmployeeExists() ENTERED : employeeEmailId : " + employeeEmailId);
		boolean checkifEmployeeExists = false;
		log.info("checkIfEmployeeExists() is under execution...");
		if (employeeEmailId == null || employeeEmailId.isEmpty() || employeeEmailId.length() == 0)
			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_EMAIL_ID_NOT_FOUND_CODE,
					ErrorCodeMessages.ERR_EMP_EMAIL_ID_NOT_FOUND_MSG);
		Integer count = employeeRepository.checkIfEmployeeExists(employeeEmailId);
		if (count > 0)
			checkifEmployeeExists = true;
		log.info("checkIfEmployeeExists() executed successfully");
		return checkifEmployeeExists;
	}

	@Transactional
	@Override
	public boolean deleteAllEmployeesById(List<Integer> employeeIds) {
		if(employeeIds.isEmpty()) {
			log.info("deleteAllEmployeesById(): employee Ids is null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_DESG_IDS_LIST_IS_EMPTY_CODE, 
					ErrorCodeMessages.ERR_DESG_IDS_LIST_IS_EMPTY_MSG);
		}
		log.info("EmployeeServiceImpl.deleteAllEmployeesById() ENTERED : employeeIds : ");
		boolean isDeleted = Boolean.FALSE;
		log.info("deleteAllEmployeesById() is under execution...");
		List<Employee> employeeList = employeeRepository.findAllById(employeeIds);
		employeeList.forEach(employee ->{
			employee.setEmployeeStatus(AdminConstants.STATUS_IN_ACTIVE);
			
		});
		employeeRepository.saveAll(employeeList);
		HttpEntity<List<Employee>> hentity = new HttpEntity<List<Employee>>(employeeList);
		log.info("calling the user microservice to change the status to Inactive");
		ResponseEntity<Boolean> entity = restTemplate.exchange(this.usersMicroservicerURL+"/all-status", HttpMethod.PUT, hentity, Boolean.class);
	    //employeeRepository.deleteAllById(employeeIds);
		isDeleted = Boolean.TRUE;
		log.info("deleteAllEmployeesById() executed successfully");
		return isDeleted;
	}

	@Override
	public List<Employee> getEmployeeReporteesData(String emailId) {
		log.info("getEmployeeReporteesData() is entered with args: emailId - "+emailId);
		if(Strings.isNullOrEmpty(emailId) || emailId.isEmpty()) {
			log.info("getEmployeeReporteesData(): employee email Id is null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_EMAIL_ID_NOT_FOUND_CODE, 
					ErrorCodeMessages.ERR_EMP_EMAIL_ID_NOT_FOUND_MSG);
		}
		log.info("getEmployeeReporteesData() is under execution...");
		var employeeReporteesList = employeeRepository.findEmployeeReportees(emailId);
		log.info("getEmployeeReporteesData() executed successfully");
		return employeeReporteesList;
	}
	
	@Override
	public List<Employee> getAllEmployeesWithUserStatus(boolean isUser) {
		log.info("getAllEmployeesWithUserStatus() is entered");
		List<Employee> employeesList = null;
		log.info("getAllEmployeesWithUserStatus() is under execution...");
		employeesList = employeeRepository.findAllEmployeesWithUserStatus(isUser);
		List<Employee> employeesData = new  ArrayList<>();
		employeesList.forEach(employee ->{
			if(employee.getEmployeeStatus().equals("Active")) {
				employeesData.add(employee);
			}
		});
		employeesList.forEach(employee -> {
			try {
				log.info("Calling Department Microservice !");
					restTemplate.getForObject(
						this.departmentMicroservicerURL + employee.getDepartmentId(), DepartmentVO.class);
					
			} catch (RestClientException restClientException) {
				log.info("Exception Occured while calling Department Microservice !");
				throw new BusinessException(ErrorCodeMessages.ERR_EMP_DEPT_REST_CLIENT_EXCEPTION_CODE,
						ErrorCodeMessages.ERR_EMP_DEPT_REST_CLIENT_EXCEPTION_MSG);
			}
		});
		log.info("getAllEmployeesWithUserStatus() executed successfully");
		return employeesData;
	}
	
	@Transactional
	@Override
	public void updateEmployeeStatus(String email) {
		if(Strings.isNullOrEmpty(email) || email.isEmpty()) {
			log.info("updateEmployeeStatus(): employee email Id is null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_EMAIL_ID_NOT_FOUND_CODE,
					ErrorCodeMessages.ERR_EMP_EMAIL_ID_NOT_FOUND_MSG);
		}
		log.info("updateEmployeeStatus() is entered");
		Employee dbUser = null;
		log.info("updateEmployeeStatus() is under execution...");
		Optional<Employee> optEmployee = employeeRepository.findByEmail(email);
		if(optEmployee.isPresent()) {
			dbUser = optEmployee.get();
		}
		dbUser.setUser(true);
		log.info("updateEmployeeStatus() executed successfully");
	}
	
	@Transactional
	@Override
	public void updateEmployeeStatustoFalse(String email) {
		if(Strings.isNullOrEmpty(email) || email.isEmpty()) {
			log.info("updateEmployeeStatustoFalse(): employee email Id is null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_EMAIL_ID_NOT_FOUND_CODE,
					ErrorCodeMessages.ERR_EMP_EMAIL_ID_NOT_FOUND_MSG);
		}
		log.info("updateEmployeeStatustoFalse() is entered");
		Employee dbUser = null;
		log.info("updateEmployeeStatustoFalse() is under execution...");
		Optional<Employee> optEmployee = employeeRepository.findByEmail(email);
		if(optEmployee.isPresent()) {
			dbUser = optEmployee.get();
		}
		dbUser.setUser(false);
		log.info("updateEmployeeStatustoFalse() executed successfully");
	}

	@Override
	public List<Employee> getAllEmployeesByEmailIds(List<String> emailIds) {
		/*emailIds.forEach(email ->{
			email = email.replaceAll("[^\\p{Print}]", ""); 
			System.out.println(email);
		});*/
		log.info("getAllEmployeesByEmailIds() is entered");
		if(emailIds == null || emailIds.isEmpty() ) {
			log.info("getAllEmployeesByEmailIds(): employee emailIds is null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_ENTITY_IS_NULL_CODE,
					ErrorCodeMessages.ERR_EMP_ENTITY_IS_NULL_MSG);
		}
		log.info("getAllEmployeesByEmailIds() is under execution...");
		List<Employee> employeeList = employeeRepository.findAllEmployeesByEmailList(emailIds);
		log.info("getAllEmployeesByEmailIds() executed successfully");
		return  employeeList;
		
	}

	@Override
	public boolean getEmployeesByEmployeeOrgId(String employeeOrgId) {
		log.info("getEmployeesByEmployeeOrgId() is entered with args : employeeOrgId - "+ employeeOrgId);
		if(Strings.isNullOrEmpty(employeeOrgId) || employeeOrgId.isEmpty()) {
			log.info("getEmployeesByEmployeeOrgId() EmptyInputException : employee orgId is empty or null.");
			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_EMPORGID_NOT_FOUND_CODE, 
					ErrorCodeMessages.ERR_EMP_EMPORGID_NOT_FOUND_MSG);
		}
		boolean employeeOrgIdstatus= Boolean.FALSE;
		log.info("getEmployeesByEmployeeOrgId() is under execution...");
		List<Employee> employeeOrgIdList=employeeRepository.findByEmployeeOrgId(employeeOrgId);
		if(employeeOrgIdList.isEmpty()) {
			 employeeOrgIdstatus = Boolean.TRUE;
		}
		log.info("getEmployeesByEmployeeOrgId() executed successfully");
		return employeeOrgIdstatus;
	}

	@Override
	public List<Employee> getAllSubordinateOfEmployee(String emailId) {
		if(Strings.isNullOrEmpty(emailId)) {
			log.info("getAllSubordinateOfEmployee() : employee email Id is null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_EMAILID_IS_EMPTY_CODE,
					ErrorCodeMessages.ERR_EMP_EMAILID_IS_EMPTY_MSG);
		}
		List<Employee> employeeList=employeeRepository.getAllEmployeesUnderThisMail(emailId);
		employeeList.addAll(employeeRepository.getEmployeeByEmail(emailId));
		return employeeList;
	}
		
	public List<Employee> getActiveEmployees() {
		log.info("getEmployeesByStatus() entered with args : status");
		log.info("getEmployeesByStatus() is under execution...");
		List<Employee> activeEmployeesList = employeeRepository.findByEmployeeStatus(AdminConstants.STATUS_ACTIVE);
		log.info("getEmployeesByStatus() executed successfully");
		return activeEmployeesList;
	}
	
	public TeamsUserProfileVO getAzureOrganizationalUser(String emailId){
		if(Strings.isNullOrEmpty(emailId)) {
			log.info("getAzureOrganizationalUser() : employee email Id is null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_EMAILID_IS_EMPTY_CODE,
					ErrorCodeMessages.ERR_EMP_EMAILID_IS_EMPTY_MSG);
		}
		TeamsUserProfileVO teamsUserprofile = this.getAzureUser(emailId);
		log.info("getAzureOrganizationalUser() executed successfully.");
		return teamsUserprofile;
	}
	
}
