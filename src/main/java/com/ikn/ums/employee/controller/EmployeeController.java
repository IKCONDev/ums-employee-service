package com.ikn.ums.employee.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ikn.ums.employee.VO.EmployeeListVO;
import com.ikn.ums.employee.VO.EmployeeVO;
import com.ikn.ums.employee.VO.TeamsUserProfileVO;
import com.ikn.ums.employee.dto.EmployeeDto;
import com.ikn.ums.employee.entity.Employee;
import com.ikn.ums.employee.exception.ControllerException;
import com.ikn.ums.employee.exception.EmployeeExistsException;
import com.ikn.ums.employee.exception.EmployeeIdExistsException;
import com.ikn.ums.employee.exception.EmptyInputException;
import com.ikn.ums.employee.exception.EntityNotFoundException;
import com.ikn.ums.employee.exception.ErrorCodeMessages;
import com.ikn.ums.employee.service.EmployeeService;
import com.netflix.servo.util.Strings;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/employees")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	/**
	 * save a manually created employee object into UMS DB
	 * 
	 * @param employee
	 * @return
	 */
	@PostMapping("/saveEmployee")
	public ResponseEntity<EmployeeDto> saveEmployee(@RequestBody EmployeeDto employee) {
		log.info("saveEmployee() is entered with args: employee - " + employee);
		if (employee == null) {
			log.info("saveEmployee() : employee Object is NULL !");
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_EMP_ENTITY_IS_NULL_CODE,
					ErrorCodeMessages.ERR_EMP_ENTITY_IS_NULL_MSG);
		}
		try {
			log.info("saveEmployee() is under execution...");
			EmployeeDto employeeSaved = employeeService.saveEmployee(employee);
			log.info("saveEmployee() : Post Employee method calling .... " + employeeSaved);
			log.info("saveEmployee() executed successfully");
			return new ResponseEntity<EmployeeDto>(employeeSaved, HttpStatus.CREATED);
		}catch (EntityNotFoundException | EmployeeExistsException | EmployeeIdExistsException businessException) {
			log.error("saveEmployee() : Exception Occured !" + businessException.getMessage(), businessException);
			throw businessException;
		} 
		catch (Exception e) {
			log.error("saveEmployee() : Exception Occured !" + e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_EMP_SAVE_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_EMP_SAVE_UNSUCCESS_MSG);
		}
	}

	@PutMapping("/update")
	public ResponseEntity<EmployeeDto> updateEmployee(@RequestBody EmployeeDto employee) {
		log.info("updateEmployee() ENTERED "+employee);
		log.info("the epmployee from front is"+" "+ employee);
		if (employee == null)
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_EMP_ENTITY_IS_NULL_CODE,
					ErrorCodeMessages.ERR_EMP_ENTITY_IS_NULL_MSG);
		try {
			log.info("updateEmployee() is under execition...");
			EmployeeDto updatedEmployee = employeeService.updateEmployee(employee);
			log.info("updateEmployee() executed successfully");
			return new ResponseEntity<EmployeeDto>(updatedEmployee, HttpStatus.CREATED);
		}catch (EntityNotFoundException | EmployeeIdExistsException businesException) {
			log.error("updateEmployee() exited with exception :Business Exception occured while updating employee. "+businesException.getMessage(), businesException);
			throw businesException;
		}
		catch (Exception e) {
			log.error("updateEmployee() : Exception Occured !" + e.getMessage(),e);
			throw new ControllerException(ErrorCodeMessages.ERR_EMP_UPDATE_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_EMP_UPDATE_UNSUCCESS_MSG);
		}
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Boolean> deleteEmployee(@PathVariable("id") Integer employeeId) {
		log.info("deleteEmployee() ENTERED : employeeId : " + employeeId);
		if (employeeId <= 0)
			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_ID_NOT_FOUND_CODE,
					ErrorCodeMessages.ERR_EMP_ID_NOT_FOUND_MSG);
		try {
			log.info("deleteEmployee() is under execution...");
			employeeService.deleteEmployee(employeeId);
			log.info("deleteEmployee() executed successfully");
			return ResponseEntity.ok(true);
		} catch (Exception e) {
			log.error("deleteEmployee() : Exception Occured while deleting employee !"
					+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_EMP_DELETE_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_EMP_DELETE_UNSUCCESS_MSG);
		}
	}

	@GetMapping("/get/{id}")
	public ResponseEntity<EmployeeVO> getEmployeeWithDepartment(@PathVariable("id") Integer employeeId) {
		log.info("getEmployeeWithDepartment() ENTERED : employeeId : " + employeeId);
		EmployeeVO employeeVO = null;
		if (employeeId <= 0)
			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_ID_NOT_FOUND_CODE,
					ErrorCodeMessages.ERR_EMP_ID_NOT_FOUND_MSG);
		try {
			log.info("getEmployeeWithDepartment() is under execution...");
			employeeVO = employeeService.getEmployeeWithDepartment(employeeId);
			log.info("getEmployeeWithDepartment() executed successfully");
			return new ResponseEntity<>(employeeVO, HttpStatus.OK);
		} catch (Exception e) {
			log.error("getEmployeeWithDepartment() : Exception Occured while getting Employee Details !"
							+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_CODE,
					ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_MSG);
		}
	}

	/**
	 * used while user authenticates into UMS application
	 * 
	 * @param email
	 * @return employee object.
	 */
	@GetMapping("/{email}")
	public ResponseEntity<EmployeeVO> getEmployeeDetailsWithDepartment(@PathVariable String email) {
		log.info("getEmployeeDetailsWithDepartment() ENTERED : email : " + email);
		if(Strings.isNullOrEmpty(email)) {
			log.info("getEmployeeDetailsWithDepartment() : employee email is null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_EMAIL_ID_NOT_FOUND_CODE,
					ErrorCodeMessages.ERR_EMP_EMAIL_ID_NOT_FOUND_MSG);
		}
		try {
			log.info("getEmployeeDetailsWithDepartment() is under execution...");
			EmployeeVO employeeDto = employeeService.fetchEmployeeDetailsWithDepartment(email);
			log.info("getEmployeeDetailsWithDepartment() executed successfully");
			return new ResponseEntity<>(employeeDto, HttpStatus.OK);
		} catch (Exception e) {
			log.error("getEmployeeDetailsWithDepartment() : Exception Occured while getting Employee Details !"
					+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_CODE,
					ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_MSG);
		}
	}

	/**
	 * can use this method for first time when no user are present in DB.
	 * 
	 * @return
	 */
	@PostMapping("/save-all")
	public ResponseEntity<Integer> saveAllAzureUserProfiles() {
		log.info("saveAllAzureUserProfiles() ENTERED ");
		try {
			log.info("saveAllAzureUserProfiles() is under execution...");
			int insertedUsersCount = employeeService.saveAzureUsers();
			log.info("saveAllAzureUserProfiles() executed successfully");
			return new ResponseEntity<>(insertedUsersCount, HttpStatus.CREATED);
		} catch (Exception e) {
			log.error("saveAllAzureUserProfiles() is exited with Exception"+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_EMP_SAVE_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_EMP_SAVE_UNSUCCESS_MSG);
		}
	}

	/**
	 * save single azure user profile based using this method
	 * 
	 * @param userPrincipalName
	 * @return
	 */
	@PostMapping("/save/{userPrincipalName}")
	public ResponseEntity<String> saveAzureUserProfile(@PathVariable("userPrincipalName") String emailId) {
		log.info("saveAzureUserProfile() ENTERED : userPrincipalName or emailId : " + emailId);
		if(Strings.isNullOrEmpty(emailId)) {
			log.info("saveAzureUserProfile() : employee email is null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_EMAIL_ID_NOT_FOUND_CODE,
					ErrorCodeMessages.ERR_EMP_EMAIL_ID_NOT_FOUND_MSG);
		}
		try {
			// employeeService.searchEmployeeByEmail(userPrincipalName);
			log.info("saveAzureUserProfile() is under execution...");
			boolean checkIfAzureUserExists = employeeService.checkIfEmployeeExists(emailId);
			if (!checkIfAzureUserExists) {
				String message = employeeService.saveAzureUser(emailId);
				return new ResponseEntity<>(message, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>("User already exists in Database, duplicate users not allowed",
						HttpStatus.NOT_ACCEPTABLE);
			}
		} catch (Exception e) {
			log.error("saveAzureUserProfile() is exited with exception:"+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_EMP_SAVE_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_EMP_SAVE_UNSUCCESS_MSG);
		}
	}

	/**
	 * fetch all employees from UMS DB -  this method is used by batch processing
	 * microservice
	 * 
	 * @return
	 */
	@GetMapping("/get-all")
	public ResponseEntity<EmployeeListVO> getAllEmployees() {
		log.info("getAllEmployees() is  ENTERED");	
		try {
			log.info("getAllEmployees()  is under execution...");
			List<Employee> employeesDbList = employeeService.getActiveEmployees();
			EmployeeListVO empListVO = new EmployeeListVO();
			empListVO.setEmployee(employeesDbList);
			log.info("getAllEmployees() executed successfully");
			return new ResponseEntity<>(empListVO, HttpStatus.OK);
		}catch (Exception e) {
			log.error("getAllEmployees() exited with exception:"+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_CODE,
					ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_MSG);
		}
	
	}
	
	@GetMapping("/all")
	public ResponseEntity<List<Employee>> getAllEmployeesDetails() {
		log.info("getAllEmployeesDetails() ENTERED");
		try {
			log.info("getAllEmployeesDetails()  is under execution...");
			List<Employee> employeesList = employeeService.getAllEmployees();
			log.info("getAllEmployeesDetails() executed successfully");
			return new ResponseEntity<>(employeesList, HttpStatus.OK);
		}catch (Exception e) {
			log.error("getAllEmployeesDetails() is exited with Exception"+ e.getMessage(), e);
		    throw new ControllerException(ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_CODE,
		    		ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_MSG);
		}
		
	}
	
	@DeleteMapping("/deleteAll/{ids}")
	public ResponseEntity<Boolean> deleteAllEmployeesById(@PathVariable("ids") String employeeIds ){
		log.info("deleteAllEmployeesById() ENTERED with employeeIds");
		if(Strings.isNullOrEmpty(employeeIds)){
			log.info("deleteAllEmployeesById(): employeeIds is null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_IDS_LIST_IS_EMPTY_CODE,
					ErrorCodeMessages.ERR_EMP_IDS_LIST_IS_EMPTY_MSG);
			
		}
		List<Integer> idList = null;
		//if(employeeIds != "" ) {
		if(!employeeIds.isEmpty()) {	
				String[] idFromUI = employeeIds.split(",");
				List<String> list = Arrays.asList(idFromUI);
				idList = list.stream()
						.map(s-> Integer.parseInt(s))
						.collect(Collectors.toList());
		}
		try {
			log.info("deleteAllEmployeesById()is under execution...");
			boolean isDeleted = employeeService.deleteAllEmployeesById(idList);
			log.info("deleteAllEmployeesById() executed successfully");
			return  new ResponseEntity<>(isDeleted,HttpStatus.OK);
			 
		}catch (EmptyInputException businessException) {
			log.error("deleteAllEmployeesById() : Exception Occured !" + businessException.getMessage(), businessException);
			throw businessException;
		}
		catch (Exception e) {
			log.error("deleteAllEmployeesById() is exited with exception"+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_EMP_DELETE_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_EMP_DELETE_UNSUCCESS_MSG);
		}
	
	}
	
	@GetMapping("/getemployee-status/{isUser}")
	public ResponseEntity<List<Employee>> getAllEmployeeWithUserStatus(@PathVariable boolean isUser){
		log.info("getAllEmployeeWithUserStatus() ENTERED" + isUser);
		log.info("getAllEmployeeWithUserStatus() is under execution...");
		try {
			List<Employee> employeeList = employeeService.getAllEmployeesWithUserStatus(isUser);
			log.info("getAllEmployeeWithUserStatus() is executed successfully");
			return new ResponseEntity<>(employeeList,HttpStatus.OK);
			
		}catch (Exception e) {
			log.error("getAllEmployeeWithUserStatus() is exited with exception"+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_CODE,
					ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_MSG);
		}
			
	}	
	@PutMapping("/employeestatus-update/{email}")
	public ResponseEntity<Boolean> updateEmployeeStatus(@PathVariable("email") String email){
		if(Strings.isNullOrEmpty(email)) {
			log.info("updateEmployeeStatus() : employee email is null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_EMAIL_ID_NOT_FOUND_CODE,
					ErrorCodeMessages.ERR_EMP_EMAIL_ID_NOT_FOUND_MSG);
		}
		log.info("updateEmployeeStatus() ENTERED with args-"+ email);
		log.info("updateEmployeeStatus() is under execution...");
		System.out.println("updateEmployeeStatus() to true is entered");
		try {
			employeeService.updateEmployeeStatus(email);
			log.info("EmployeeController.updateEmployeeStatus() executed successfully");
			return new ResponseEntity<>(true,HttpStatus.OK);
		}catch (Exception e) {
			log.error("updateEmployeeStatus() is exited with exception"+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_EMP_UPDATE_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_EMP_UPDATE_UNSUCCESS_MSG);
		}
		
	}
	@GetMapping("/{emailId}/reportees")
	public ResponseEntity<List<Employee>> getEmployeeReportees(@PathVariable String emailId){
		log.info("getEmployeeReportees() ENTERED with args :"+ emailId);
		if(Strings.isNullOrEmpty(emailId)) {
			log.info("getEmployeeReportees() : employee email is null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_EMAIL_ID_NOT_FOUND_CODE,
					ErrorCodeMessages.ERR_EMP_EMAIL_ID_NOT_FOUND_MSG);
		}
		log.info("getEmployeeReportees() is under execution...");
		try {
			List<Employee> reporteesList = employeeService.getEmployeeReporteesData(emailId);
			log.info("getEmployeeReportees() executed successfully");
			return new ResponseEntity<>(reporteesList, HttpStatus.OK);
			
		}catch (Exception e) {
			log.error("getEmployeeReportees() is exited with exception"+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_CODE,
					ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_MSG);
		}
		

	}
	@PutMapping("/status-update/{email}")
	public ResponseEntity<Boolean> updateEmployeeStatustoFalse(@PathVariable("email") String email){
		log.info("updateEmployeeStatustoFalse() ENTERED with args :"+ email);
		if(Strings.isNullOrEmpty(email)) {
			log.info("updateEmployeeStatustoFalse() : employee email is null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_EMAIL_ID_NOT_FOUND_CODE,
					ErrorCodeMessages.ERR_EMP_EMAIL_ID_NOT_FOUND_MSG);
		}
		log.info("updateEmployeeStatustoFalse() is under execution...");
		try {
			employeeService.updateEmployeeStatustoFalse(email);
			log.info("EmployeeController.updateEmployeeStatustoFalse() executed successfully");
			return new ResponseEntity<>(true,HttpStatus.OK);
			
		}catch (Exception e) {
			log.error("updateEmployeeStatustoFalse() is exited with exception:"+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_EMP_UPDATE_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_EMP_UPDATE_UNSUCCESS_MSG);
		}
		
	}
	@GetMapping("/attendees/{emailIds}")
	public ResponseEntity<List<EmployeeDto>> getAllEmployeeByEmailIds(@PathVariable("emailIds") String emailIds){
		log.info("getAllEmployeeByEmailIds() ENTERED with args:"+ emailIds);
		if(Strings.isNullOrEmpty(emailIds)) {
			log.info("getAllEmployeeByEmailIds() : employee email Ids is null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_IDS_LIST_IS_EMPTY_CODE,
					ErrorCodeMessages.ERR_EMP_IDS_LIST_IS_EMPTY_MSG);
		}
		log.info("getAllEmployeeByEmailIds() is under execution...");
		List<String> emailList = null;
		if (!emailIds.isEmpty() && emailIds.contains(",")) {
		    String[] idFromUI = emailIds.split(",");
		    emailList = Arrays.asList(idFromUI);
		}
		else {
			emailList = Arrays.asList(emailIds);
		}
		try {
			List<EmployeeDto> employeeList = employeeService.getAllEmployeesByEmailIds(emailList);
			log.info("getAllEmployeeByEmailIds() executed successfully");
			return new ResponseEntity<>(employeeList,HttpStatus.OK);
		}catch (Exception e) {
			log.error("getAllEmployeeByEmailIds is exited wit exception:" + e.getMessage(), e);
		    throw new ControllerException(ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_CODE, 
		    		ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_MSG);
		    
		}
		
	}
	
	@GetMapping("/employee-head/{emailId}")
	public ResponseEntity<List<Employee>> getAllEmployeeBasedOnTheDepartmentHead(@PathVariable("emailId") String emailId){
		if(Strings.isNullOrEmpty(emailId)) {
			log.info("getAllEmployeeBasedOnTheDepartmentHead() : employee email Id is null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_EMAILID_IS_EMPTY_CODE,
					ErrorCodeMessages.ERR_EMP_EMAILID_IS_EMPTY_MSG);
		}
		try {
		List<Employee> employeeList = employeeService.getAllSubordinateOfEmployee(emailId);
		return new ResponseEntity<>(employeeList,HttpStatus.OK);
		}catch (Exception e) {
			log.error("getAllEmployeeBasedOnTheDepartmentHead is exited wit exception:" + e.getMessage(), e);
		    throw new ControllerException(ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_CODE, 
		    		ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_MSG);
		    
		}
	}
	
	@GetMapping("/teamsProfile/{emailId}")
	public ResponseEntity<TeamsUserProfileVO> getTeamsUserProfile(@PathVariable("emailId") String emailId){
		if(Strings.isNullOrEmpty(emailId)) {
			log.info("getTeamsUserProfile() : employee email Id is null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_EMAILID_IS_EMPTY_CODE,
					ErrorCodeMessages.ERR_EMP_EMAILID_IS_EMPTY_MSG);
		}
		try {
		TeamsUserProfileVO userProfile = employeeService.getAzureOrganizationalUser(emailId);
		return new ResponseEntity<>(userProfile,HttpStatus.OK);
		}catch (EmptyInputException businessException) {
			log.error("getTeamsUserProfile() is exited with business exception:" + businessException.getMessage(), businessException);
			throw businessException;
		}
		catch (Exception e) {
			log.error("getTeamsUserProfile() is exited with exception:" + e.getMessage(), e);
		    throw new ControllerException(ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_CODE, 
		    		ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_MSG);
		    
		}
	}
	
	@GetMapping("/all/{departmentId}/{isUser}")
	public ResponseEntity<List<EmployeeDto>> getAllEmployeesOfDepartment(@PathVariable() Long departmentId,
			@PathVariable() boolean isUser) {
		log.info("getAllEmployeesOfDepartment() is  ENTERED");	
		if(departmentId < 1 || departmentId == null) {
			log.info("getAllEmployeesOfDepartment() : EmptyInputException - departmentId is empty / null.");
			throw new EmptyInputException(ErrorCodeMessages.ERR_DEPT_ID_EMPTY_CODE, 
					ErrorCodeMessages.ERR_DEPT_ID_EMPTY_MSG);
		}
		try {
			log.info("getAllEmployeesOfDepartment()  is under execution...");
			List<EmployeeDto> employeesOfDepartmentList = employeeService.getEmployeesOfDepartment(departmentId,isUser);
			log.info("getAllEmployeesOfDepartment() executed successfully");
			return new ResponseEntity<>(employeesOfDepartmentList, HttpStatus.OK);
		}catch (Exception e) {
			log.error("getAllEmployeesOfDepartment() exited with exception:"+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_CODE,
					ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_MSG);
		}
	
	}
	
	@GetMapping("/associatedEmployees/{teamId}/{isUser}")
	public ResponseEntity<List<EmployeeDto>> getAllEmployeesOfTeam(@PathVariable() Long teamId,
			@PathVariable() boolean isUser) {
		log.info("getAllEmployeesOfDepartment() is  ENTERED");	
		if(teamId < 1 || teamId == null) {
			log.info("getAllEmployeesOfDepartment() EmptyInputException - teamid is empty / null.");	
			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_TEAMID_EMPTY_CODE, 
					ErrorCodeMessages.ERR_EMP_TEAMID_EMPTY_MSG);
		}
		try {
			log.info("getAllEmployeesOfDepartment()  is under execution...");
			List<EmployeeDto> employeesOfDepartmentList = employeeService.getEmployeesByTeamId(teamId, isUser);
			log.info("getAllEmployeesOfDepartment() executed successfully");
			return new ResponseEntity<>(employeesOfDepartmentList, HttpStatus.OK);
		}catch (EmptyInputException e) {
			log.error("getAllEmployeesOfDepartment() exited with Business exception:"+ e.getMessage(), e);
			throw e;
		}
		catch (Exception e) {
			log.error("getAllEmployeesOfDepartment() exited with exception:"+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_CODE,
					ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_MSG);
		}
	
	}
	
	@GetMapping("/respectiveemployees/{teamIds}")
	public ResponseEntity<List<EmployeeDto>> getAllEmployeesOfTeamByTeamIds(@PathVariable() List<Integer> teamIds) {
		log.info("getAllEmployeesOfTeamByTeamIds() is  ENTERED");	
		try {
			log.info("getAllEmployeesOfTeamByTeamIds()  is under execution...");
			List<EmployeeDto> employeesOfDepartmentList = employeeService.getAllEmployeesByTeamIds(teamIds);
			log.info("getAllEmployeesOfTeamByTeamIds() executed successfully");
			return new ResponseEntity<>(employeesOfDepartmentList, HttpStatus.OK);
		}catch (Exception e) {
			log.error("getAllEmployeesOfTeamByTeamIds() exited with exception:"+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_CODE,
					ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_MSG);
		}
	
	}
	
	@GetMapping("/teamofemployee/{emailId}")
	public ResponseEntity<EmployeeDto> getAllEmployeeswithTeamName(@PathVariable() String emailId) {
		log.info("getAllEmployeeswithTeamName() is  ENTERED");
		try {
			log.info("getAllEmployeeswithTeamName()  is under execution...");
			EmployeeDto employeeswithTeamName = employeeService.getEmployeeTeamName(emailId);
			log.info("getAllEmployeeswithTeamName() executed successfully");
			return new ResponseEntity<>(employeeswithTeamName, HttpStatus.OK);
		} catch (Exception e) {
			log.error("getAllEmployeeswithTeamName() exited with exception:" + e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_CODE,
					ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_MSG);
		}

	}
	
	@GetMapping("/teamReportees/{emailId}")
	public ResponseEntity<List<EmployeeDto>> getEmployeeByTeamLead(@PathVariable() String emailId) {
		log.info("getEmployeeByTeamLead() is  ENTERED");
		try {
			log.info("getEmployeeByTeamLead()  is under execution...");
			List<EmployeeDto> employeesOfteam = employeeService.getReporteesOfTeamLead(emailId);
			log.info("getEmployeeByTeamLead() executed successfully");
			return new ResponseEntity<>(employeesOfteam, HttpStatus.OK);
		} 
		catch (EntityNotFoundException e) {
			log.error("getEmployeeByTeamLead() exited with exception:" + e.getMessage(), e);
			throw e;
		}catch (Exception e) {
			log.error("getEmployeeByTeamLead() exited with exception:" + e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_CODE,
					ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_MSG);
		}

	}
	
	
	

}
