package com.ikn.ums.employee.controller;

import java.util.ArrayList;
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
import com.ikn.ums.employee.entity.Employee;
import com.ikn.ums.employee.exception.ControllerException;
import com.ikn.ums.employee.exception.EmployeeExistsException;
import com.ikn.ums.employee.exception.EmptyInputException;
import com.ikn.ums.employee.exception.EntityNotFoundException;
import com.ikn.ums.employee.exception.ErrorCodeMessages;
import com.ikn.ums.employee.service.EmployeeService;
import com.ikn.ums.employee.service.impl.EmployeeServiceImpl;

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
//	@PostMapping("/save")
//	public Employee saveEmployee(@RequestBody Employee employee) {
//		log.info("EmployeeController.saveEmployee() ENTERED");
//		return employeeService.saveEmployee(employee);
//	}

	/**
	 * save a manually created employee object into UMS DB
	 * 
	 * @param employee
	 * @return
	 */
	@PostMapping("/saveEmployee")
	public ResponseEntity<?> saveEmployee(@RequestBody Employee employee) {
		log.info("saveEmployee() ENTERED" + employee);
		if (employee == null) {
			log.info("saveEmployee() : employee Object is NULL !");
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_EMP_ENTITY_IS_NULL_CODE,
					ErrorCodeMessages.ERR_EMP_ENTITY_IS_NULL_MSG);
		}
		try {
			log.info("saveEmployee() is under execution...");
			Employee newEmp= new Employee();
			newEmp=employee;
			String employeeOrgId=newEmp.getEmployeeOrgId();
			if(employeeService.getEmployeesByEmployeeOrgId(employeeOrgId)) {
			Employee employeeSaved = employeeService.saveEmployee(employee);
			log.info("saveEmployee() : Post Employee method calling .... " + employeeSaved);
			log.info("saveEmployee() executed successfully");
			return new ResponseEntity<Employee>(employeeSaved, HttpStatus.CREATED);
			}else return new ResponseEntity<Employee>(HttpStatus.NOT_ACCEPTABLE);
		}catch (EntityNotFoundException | EmployeeExistsException businessException) {
			log.error("saveEmployee() : Exception Occured !" + businessException.getMessage(), businessException);
			throw businessException;
		} 
		catch (Exception e) {
			log.error("saveEmployee() : Exception Occured !" + e.fillInStackTrace());
			throw new ControllerException(ErrorCodeMessages.ERR_EMP_SAVE_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_EMP_SAVE_UNSUCCESS_MSG);
		}
	}

	@PutMapping("/update")
	public ResponseEntity<Employee> updateEmployee(@RequestBody Employee employee) {
		log.info("updateEmployee() ENTERED "+employee);
		log.info("the epmployee from front is"+" "+ employee);
		Employee updatedEmployee = new Employee();
		if (employee == null)
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_EMP_ENTITY_IS_NULL_CODE,
					ErrorCodeMessages.ERR_EMP_ENTITY_IS_NULL_MSG);
		try {
			log.info("updateEmployee() is under execition...");
			updatedEmployee = employeeService.updateEmployee(employee);
			return new ResponseEntity<Employee>(updatedEmployee, HttpStatus.CREATED);
		} catch (Exception e) {
			log.error("updateEmployee() : Exception Occured !" + e.fillInStackTrace());
			throw new ControllerException(ErrorCodeMessages.ERR_EMP_UPDATE_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_EMP_UPDATE_UNSUCCESS_MSG);
		}
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteEmployee(@PathVariable("id") Integer employeeId) {
		log.info("deleteEmployee() ENTERED : employeeId : " + employeeId);
		if (employeeId <= 0)
			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_ID_NOT_FOUND_CODE,
					ErrorCodeMessages.ERR_EMP_ID_NOT_FOUND_MSG);
		try {
			log.info("deleteEmployee() is under execution...");
			employeeService.deleteEmployee(employeeId);
			return ResponseEntity.ok(true);
		} catch (Exception e) {
			log.error("deleteEmployee() : Exception Occured while deleting employee !"
					+ e.fillInStackTrace());
			throw new ControllerException(ErrorCodeMessages.ERR_EMP_DELETE_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_EMP_DELETE_UNSUCCESS_MSG);
		}
	}

	@GetMapping("/get/{id}")
	public ResponseEntity<?> getEmployeeWithDepartment(@PathVariable("id") Integer employeeId) {
		log.info("getEmployeeWithDepartment() ENTERED : employeeId : " + employeeId);
		EmployeeVO employeeVO = null;
		System.out.println("getEmployeeWithDepartment() ENTERED ");
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
							+ e.fillInStackTrace());
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
	public ResponseEntity<?> getEmployeeDetailsWithDepartment(@PathVariable String email) {
		log.info("getEmployeeDetailsWithDepartment() ENTERED : email : " + email);
		System.out.println("getEmployeeDetailsWithDepartment() : email : " + email);
		try {
			log.info("getEmployeeDetailsWithDepartment() is under execution...");
			System.out.println(email);
			EmployeeVO employeeDto = employeeService.fetchEmployeeDetailsWithDepartment(email);
			System.out.println("getEmployeeDetailsWithDepartment() post retrieval" + employeeDto.getEmail());
			System.out.println(employeeDto);
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
	public ResponseEntity<?> saveAllAzureUserProfiles() {
		log.info("EmployeeController.saveAllAzureUserProfiles() ENTERED ");
		try {
			log.info("saveAllAzureUserProfiles() is under execution...");
			int insertedUsersCount = employeeService.saveAzureUsers();
			log.info("saveAllAzureUserProfiles() executed successfully");
			return new ResponseEntity<>(insertedUsersCount, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("saveAllAzureUserProfiles() is exited with Exception"+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_EMP_SAVE_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_EMP_SAVE_UNSUCCESS_MSG);
			// TODO: Check the above
		}
	}

	/**
	 * save single azure user profile based using this method
	 * 
	 * @param userPrincipalName
	 * @return
	 */
	@PostMapping("/save/{userPrincipalName}")
	public ResponseEntity<?> saveAzureUserProfile(@PathVariable("userPrincipalName") String emailId) {
		log.info("saveAzureUserProfile() ENTERED : userPrincipalName or emailId : " + emailId);
		try {
			// Integer dbEmployeeCount =
			// employeeService.searchEmployeeByEmail(userPrincipalName);
			log.info("saveAzureUserProfile() ENTERED : userPrincipalName or emailId : ");
			boolean checkIfAzureUserExists = employeeService.checkIfEmployeeExists(emailId);
			if (!checkIfAzureUserExists) {
				String message = employeeService.saveAzureUser(emailId);
				return new ResponseEntity<>(message, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>("User already exists in Database, duplicate users not allowed",
						HttpStatus.NOT_ACCEPTABLE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("saveAzureUserProfile() is exited with exception:"+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_EMP_SAVE_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_EMP_SAVE_UNSUCCESS_MSG);
		}
	}

	/**
	 * fetch all employees from UMS DB this method is used by batch processing
	 * microservice
	 * 
	 * @return
	 */
	@GetMapping("/get-all")
	public ResponseEntity<?> getAllEmployees() {
		log.info("getAllEmployees() is  ENTERED");
		log.info("getAllEmployees()  is under execution...");
		try {
			List<Employee> employeesDbList = employeeService.getAllEmployees();
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
	public ResponseEntity<?> getAllEmployeesDetails() {
		log.info("getAllEmployeesDetails() ENTERED");
		log.info("getAllEmployeesDetails()  is under execution...");
		try {
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
	public ResponseEntity<?> deleteAllEmployeesById(@PathVariable("ids") String employeeIds ){
		log.info("deleteAllEmployeesById() ENTERED with employeeIds");
		List<Integer> idList = null;
		if(employeeIds !="") {
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
			 
		}catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
			//return new ResponseEntity<>("Exception occured while deleting the employees", HttpStatus.INTERNAL_SERVER_ERROR);
			log.error("deleteAllEmployeesById() is exited with exception"+ e.getMessage(), e);
			throw new ControllerException(ErrorCodeMessages.ERR_EMP_DELETE_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_EMP_DELETE_UNSUCCESS_MSG);
		}
	
	}
	
	@GetMapping("/getemployee-status/{isUser}")
	public ResponseEntity<?> getAllEmployeeWithUserStatus(@PathVariable boolean isUser){
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
	public ResponseEntity<?> updateEmployeeStatus(@PathVariable("email") String email){
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
	public ResponseEntity<?> getEmployeeReportees(@PathVariable String emailId){
		log.info("getEmployeeReportees() ENTERED with args :"+ emailId);
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
	public ResponseEntity<?> updateEmployeeStatustoFalse(@PathVariable("email") String email){
		log.info("updateEmployeeStatustoFalse() ENTERED with args :"+ email);
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
	public ResponseEntity<?> getAllEmployeeByEmailIds(@PathVariable("emailIds") String emailIds){
		log.info("getAllEmployeeByEmailIds() ENTERED with args:"+ emailIds);
		log.info("getAllEmployeeByEmailIds() is under execution...");
		System.out.println("email"+emailIds);
		List<String> emailList = null;
		System.out.println("getAllEmployeeByEmailIds() is entered");
		if (!emailIds.isEmpty() && emailIds.contains(",")) {
			System.out.println("getAllEmployeeByEmailIds() is under exxecution...");
		    String[] idFromUI = emailIds.split(",");
		    emailList = Arrays.asList(idFromUI);
		    // idList now contains strings without converting to integers
		}
		else {
			emailList = Arrays.asList(emailIds);
		}
		emailList.forEach(email ->{
			//email = email.replaceAll("[^\\p{Print}]", ""); 
			System.out.println(email);
		});
		try {
			List<Employee> employeeList = employeeService.getAllEmployeesByEmailIds(emailList);
			log.info("EmployeeController.getAllEmployeeByEmailIds() executed successfully");
			return new ResponseEntity<>(employeeList,HttpStatus.OK);
		}catch (Exception e) {
			log.error("getAllEmployeeByEmailIds is exited wit exception:" + e.getMessage(), e);
		    throw new ControllerException(ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_CODE, 
		    		ErrorCodeMessages.ERR_EMP_DETAILS_GET_UNSUCESS_MSG);
		    
		}
		
	}
	

}
