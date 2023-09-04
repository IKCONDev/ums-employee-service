package com.ikn.ums.employee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ikn.ums.employee.entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

	//Employee findById(Integer id);
	
	//find and employee by username (email)
	Optional<Employee> findByEmail(String email);

}
