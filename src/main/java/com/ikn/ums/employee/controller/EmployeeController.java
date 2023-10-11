package com.ikn.ums.employee.controller;

import java.util.List;

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
import com.ikn.ums.employee.exception.EmptyInputException;
import com.ikn.ums.employee.exception.EntityNotFoundException;
import com.ikn.ums.employee.exception.ErrorCodeMessages;
import com.ikn.ums.employee.service.EmployeeService;

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
		log.info("EmployeeController.saveEmployee() ENTERED" + employee);
		if (employee == null) {
			log.info("EmployeeController.saveEmployee() : employee Object is NULL !");
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_EMP_ENTITY_IS_NULL_CODE,
					ErrorCodeMessages.ERR_EMP_ENTITY_IS_NULL_MSG);
		}
		try {
			Employee employeeSaved = employeeService.saveEmployee(employee);
			log.info("EmployeeController.saveEmployee() : Post Employee method calling .... " + employeeSaved);
			return new ResponseEntity<Employee>(employeeSaved, HttpStatus.CREATED);
		} catch (Exception e) {
			log.info("EmployeeController.saveEmployee() : Exception Occured !" + e.fillInStackTrace());
			throw new ControllerException(ErrorCodeMessages.ERR_EMP_SAVE_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_EMP_SAVE_UNSUCCESS_MSG);
		}
	}

	@PutMapping("/update")
	public ResponseEntity<Employee> updateEmployee(@RequestBody Employee employee) {
		log.info("EmployeeController.updateEmployee() ENTERED ");
		log.info("the epmployee from front is"+" "+ employee);
		Employee updatedEmployee = new Employee();
		if (employee == null)
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_EMP_ENTITY_IS_NULL_CODE,
					ErrorCodeMessages.ERR_EMP_ENTITY_IS_NULL_MSG);
		try {
			updatedEmployee = employeeService.updateEmployee(employee);
			return new ResponseEntity<Employee>(updatedEmployee, HttpStatus.CREATED);
		} catch (Exception e) {
			log.info("EmployeeController.updateEmployee() : Exception Occured !" + e.fillInStackTrace());
			throw new ControllerException(ErrorCodeMessages.ERR_EMP_UPDATE_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_EMP_UPDATE_UNSUCCESS_MSG);
		}
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteEmployee(@PathVariable("id") Integer employeeId) {
		log.info("EmployeeController.deleteEmployee() ENTERED : employeeId : " + employeeId);
		if (employeeId <= 0)
			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_ID_NOT_FOUND_CODE,
					ErrorCodeMessages.ERR_EMP_ID_NOT_FOUND_MSG);
		try {
			employeeService.deleteEmployee(employeeId);
			return ResponseEntity.ok("Employee deleted successfully");
		} catch (Exception e) {
			log.info("EmployeeController.deleteEmployee() : Exception Occured while deleting employee !"
					+ e.fillInStackTrace());
			throw new ControllerException(ErrorCodeMessages.ERR_EMP_DELETE_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_EMP_DELETE_UNSUCCESS_MSG);
		}
	}

	@GetMapping("/get/{id}")
	public ResponseEntity<?> getEmployeeWithDepartment(@PathVariable("id") Integer employeeId) {
		log.info("EmployeeController.getEmployeeWithDepartment() ENTERED : employeeId : " + employeeId);
		EmployeeVO employeeVO = null;
		
		System.out.println("EmployeeController.getEmployeeWithDepartment() ENTERED ");
		
		if (employeeId <= 0)
			throw new EmptyInputException(ErrorCodeMessages.ERR_EMP_ID_NOT_FOUND_CODE,
					ErrorCodeMessages.ERR_EMP_ID_NOT_FOUND_MSG);
		try {
			employeeVO = employeeService.getEmployeeWithDepartment(employeeId);
			return new ResponseEntity<>(employeeVO, HttpStatus.OK);
		} catch (Exception e) {
			log.info(
					"EmployeeController.getEmployeeWithDepartment() : Exception Occured while getting Employee Details !"
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
		log.info("EmployeeController.getEmployeeDetailsWithDepartment() ENTERED : email : " + email);
		
		System.out.println("EmployeeController.getEmployeeDetailsWithDepartment() : email : " + email);
		
		try {
			System.out.println(email);
			EmployeeVO employeeDto = employeeService.fetchEmployeeDetailsWithDepartment(email);
			
			System.out.println("EmployeeController.getEmployeeDetailsWithDepartment() post retrieval" + employeeDto.getEmail());
			
			System.out.println(employeeDto);
			return new ResponseEntity<>(employeeDto, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>("No user found with provided email " + email, HttpStatus.INTERNAL_SERVER_ERROR);
			// TODO: Check the above
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
			int insertedUsersCount = employeeService.saveAzureUsers();
			return new ResponseEntity<>(insertedUsersCount, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Users data could not be saved , Please try again",
					HttpStatus.INTERNAL_SERVER_ERROR);
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
		log.info("EmployeeController.saveAzureUserProfile() ENTERED : userPrincipalName or emailId : " + emailId);
		try {
			// Integer dbEmployeeCount =
			// employeeService.searchEmployeeByEmail(userPrincipalName);
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
			return new ResponseEntity<>("May be the user does not exist in your azure DB, please try again",
					HttpStatus.INTERNAL_SERVER_ERROR);
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
		log.info("EmployeeController.getAllEmployees() ENTERED");
		List<Employee> employeesDbList = employeeService.getAllEmployees();
		EmployeeListVO empListVO = new EmployeeListVO();
		empListVO.setEmployee(employeesDbList);
		return new ResponseEntity<>(empListVO, HttpStatus.OK);
	}

}
