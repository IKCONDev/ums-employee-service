package com.ikn.ums.employee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ikn.ums.employee.entity.Employee;
import java.util.List;


@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

	//Employee findById(Integer id);
	
	//find and employee by username (email)
	Optional<Employee> findByEmail(String email);
	
//	@Query("SELECT COUNT(*) FROM Employee WHERE email=:email")
//	Integer searchEmployeeDetailsByMail(String email);
	
	@Query("SELECT COUNT(*) FROM Employee WHERE email=:employeeEmailId")
	Integer checkIfEmployeeExists ( String employeeEmailId);
	
	@Query("FROM Employee WHERE reportingManager=:emailId")
	List<Employee> findEmployeeReportees(String emailId);
	
	@Query("FROM Employee WHERE isUser=:userStatus")
	List<Employee> findAllEmployeesWithUserStatus(boolean userStatus);

}
