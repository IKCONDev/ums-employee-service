package com.ikn.ums.employee.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.azure.core.credential.AccessToken;
import com.ikn.ums.employee.VO.DepartmentVO;
import com.ikn.ums.employee.VO.EmployeeVO;
import com.ikn.ums.employee.VO.TeamsUserProfileVO;
import com.ikn.ums.employee.entity.Employee;
import com.ikn.ums.employee.exception.UMSBusinessException;
import com.ikn.ums.employee.model.UserProfilesResponseWrapper;
import com.ikn.ums.employee.repository.EmployeeRepository;
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
	
	private AccessToken acToken = new AccessToken(this.accessToken,OffsetDateTime.now() );
	
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
		log.info("EmployeeService.saveEmployee() ENTERED");
		try {
			if ( employee == null) {
				throw new UMSBusinessException("1022", "Emplyee Object is Null or Empty");
			}
			/**
			 * In case the the handling was not done properly from front end, we need to handle in the Service Layer
			 */
			if ( employee.getFirstName().isEmpty() || employee.getFirstName().length() != 0 ) {
				throw new UMSBusinessException ("1001" , "Please send proper Employee First Name");
			}
			return employeeRepository.save(employee);
		}catch ( IllegalArgumentException e ) {
			throw new UMSBusinessException("1002","Given Employee is NULL" + e.getMessage()); //Handle exception when employee object is null
		}catch (Exception e) {
			throw new UMSBusinessException("1003", "Something went wrong in Service Layer");
		}
		//TODO: Need to cover all the Business Cases
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

	/**
	 * Retrieve the Employee Details based on the Employee ID
	 * @param Integer employeeId
	 * @return EmployeeVO
	 */
	public EmployeeVO getEmployeeWithDepartment(Integer employeeId) {

		System.out.println("EmployeeService.getUserWithDepartment() : employeeId : " + employeeId);
		log.info("EmployeeService.getUserWithDepartment() ENTERED");
		
		try {

			if ( employeeId != 0) {
				throw new UMSBusinessException("1010","Employee ID is null");
			}
			// ResponseTemplateVO responseTemplateVO = new ResponseTemplateVO();
			EmployeeVO employeeVO = new EmployeeVO();
			Optional<Employee> optEmployee = employeeRepository.findById(employeeId);
			Employee employee = optEmployee.get();
			System.out.println("EmployeeService.getUserWithDepartment() : employee.getDepartmentId() : 0 "
					+ employee.getDepartmentId());
//				Department department = restTemplate.getForObject("http://localhost:9001/departments/" + employee.getDepartmentId(), Department.class);
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
		}catch ( IllegalArgumentException e) {
			throw new UMSBusinessException("1008","Employee is null" + e.getMessage());
		}catch ( NoSuchElementException e ) {
			throw new UMSBusinessException("1009","Employee is null" + e.getMessage());
		}
		
	}

	
//	@Override
//	public List<Employee> findAllEmployees() {
//		List<Employee> employeeList = employeeRepository.findAll();
//		return employeeList;
//	}

	@Override
	public List<Employee> findAllEmployees() {
		List<Employee> employeesList = null;
		try {
			employeesList = employeeRepository.findAll();
		}catch (Exception e) {
			throw new UMSBusinessException("1005","Issue in Service Layer when fetching employees");
		}
		if (employeesList.isEmpty()) 
			throw new UMSBusinessException("1004" , "Employees List is Empty");
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
			//id will be auto generated
			e.setFirstName(profile.getDisplayName());
			e.setLastName(profile.getSurname());
			e.setTeamsUserId(profile.getUserId());
			e.setEmail(profile.getUserPrincipalName());
			e.setDesignation(profile.getJobTitle());
			e.setTwoFactorAuthentication(false);
			//setting default password, //Test@123 in encrypted format
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

	/**
	 * converts a azure user profile to UMS employee profile and saves in UMS DB.
	 */
	@Override
	public String saveAzureUser(String azureUserPrincipalName) {
		Employee e = null;
		TeamsUserProfileVO profile = getAzureUser(azureUserPrincipalName);
		Employee insertedUser = null;
		
		//check for null and convert azure user profile to UMS employee profile
		if(profile != null) {
			e = new Employee();
			//id will be auto generated
			e.setFirstName(profile.getDisplayName());
			e.setLastName(profile.getSurname());
			e.setTeamsUserId(profile.getUserId());
			e.setEmail(profile.getUserPrincipalName());
			e.setDesignation(profile.getJobTitle());
			e.setTwoFactorAuthentication(false);
			//setting default password, //Test@123 in encrypted format
			e.setEncryptedPassword("$2a$10$054UvQ85YjjEMnb2Okh9r.qJNDOE9trkRhEjeNE6tdPeeBJNEHZpa");
			e.setOtpCode(0);
			e.setDepartmentId(1L);
			//set default role
			e.setUserRole("Team Member");
			
			//save user
			insertedUser = employeeRepository.save(e);
		}
		return "User "+insertedUser.getEmail()+" saved successfully";
	}
	
	/**
	 * get a single user profile from azure active directory based on the user principal name
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
		String userProfileUrl = "https://graph.microsoft.com/v1.0/users/"+userPrincipalName;

		// prepare headers
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", "Bearer " + this.accessToken);
		httpHeaders.add("content-type", "application/json");

		// prepare http entity with headers
		HttpEntity httpEntity = new HttpEntity<>(httpHeaders);

		// prepare the rest template and hit the graph api user end point
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<TeamsUserProfileVO> userProfileResponse = restTemplate.exchange(userProfileUrl,
				HttpMethod.GET, httpEntity, TeamsUserProfileVO.class);

		// get all user profiles from reponse object
		TeamsUserProfileVO userDto = userProfileResponse.getBody();

		return userDto;
		
	}

	@Override
	public Integer searchEmployeeByEmail(String email) {
		Integer count = employeeRepository.searchEmployeeDetailsByMail(email);
		return count;
	}
	
	@Override
	public void deleteEmployee(Integer employeeId) {
		try {
			employeeRepository.deleteById(employeeId);	
		}catch (IllegalArgumentException e) {
			throw new UMSBusinessException("1020","Employee Id is null" + e.getMessage());
		}
	}
}
