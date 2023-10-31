package com.ikn.ums.employee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ikn.ums.employee.entity.Designation;

@Repository
public interface DesignationRepository extends JpaRepository<Designation, Long> {
	
	Optional<Designation> findByDesignationName(String desgName);

}
