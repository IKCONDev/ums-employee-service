package com.ikn.ums.employee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ikn.ums.employee.entity.Employee;
import java.util.List;


@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

	Optional<Employee> findByEmail(String email);
	
	@Query("SELECT COUNT(*) FROM Employee WHERE email=:employeeEmailId")
	Integer checkIfEmployeeExists ( String employeeEmailId);
	
	@Query(value = "WITH RECURSIVE RecursiveReportees AS (\r\n"
			+ "    SELECT * FROM employee_tab WHERE reporting_manager = :emailId \r\n"
			+ "    UNION\r\n"
			+ "    SELECT e.* FROM employee_tab e\r\n"
			+ "    JOIN RecursiveReportees r ON  e.reporting_manager = r.email\r\n"
			+ ")\r\n"
			+ "SELECT * FROM RecursiveReportees;", nativeQuery = true)
	List<Employee> findEmployeeReportees(String emailId);
	
	@Query("FROM Employee WHERE isUser=:userStatus")
	List<Employee> findAllEmployeesWithUserStatus(boolean userStatus);
	
	@Query("FROM Employee e WHERE e.email IN :emailList")
    List<Employee> findAllEmployeesByEmailList( List<String> emailList);

	List<Employee> findByEmployeeOrgId (String employeeOrgId);
	
	@Query("SELECT COUNT(*) FROM Employee WHERE employeeOrgId=:employeeId")
	Integer isEmployeeIDExists(String employeeId);

}
