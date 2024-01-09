package com.ikn.ums.employee.service;

import java.util.List;

import com.ikn.ums.employee.dto.DesignationDto;
import com.ikn.ums.employee.entity.Designation;

public interface DesignationService {
	
	DesignationDto createDesignation(DesignationDto designation);
	DesignationDto updateDesignation(DesignationDto designation);
	void deleteDesignationById(Long id);
	Designation getDesignationById(Long id);
	List<Designation> getAllDesignations(); 
	void deleteSelectedDesignationsByIds(List<Long> ids);
}
