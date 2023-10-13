package com.ikn.ums.employee.service;

import java.util.List;

import com.ikn.ums.employee.entity.Designation;

public interface DesignationService {
	
	Designation createDesignation(Designation designation);
	Designation updateDesignation(Designation designation);
	void deleteDesignationById(Long id);
	Designation getDesignationById(Long id);
	List<Designation> getAllDesignations(); 
	void deleteSelectedDesignationsByIds(List<Long> ids);
}
