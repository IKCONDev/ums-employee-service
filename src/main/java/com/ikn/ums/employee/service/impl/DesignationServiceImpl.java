package com.ikn.ums.employee.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ikn.ums.employee.entity.Designation;
import com.ikn.ums.employee.exception.EmptyListException;
import com.ikn.ums.employee.exception.EntityNotFoundException;
import com.ikn.ums.employee.exception.ErrorCodeMessages;
import com.ikn.ums.employee.repository.DesignationRepository;
import com.ikn.ums.employee.service.DesignationService;

@Service
public class DesignationServiceImpl implements DesignationService {
	
	@Autowired
	private DesignationRepository designationRepository;

	@Override
	public Designation createDesignation(Designation designation) {
		designation.setCreatedDateTime(LocalDateTime.now());
		Designation createdDesignation = designationRepository.save(designation);
		return createdDesignation;
	}

	@Override
	public Designation updateDesignation(Designation designation) {
		Optional<Designation> optionalDbDesignation = designationRepository.findById(designation.getId());
		Designation dbDesignation = null;
		if(optionalDbDesignation.isPresent()) {
			dbDesignation = optionalDbDesignation.get();
		}
		//map required properties to be updated
		dbDesignation.setDesignationName(designation.getDesignationName());
		dbDesignation.setModifiedBy(designation.getModifiedBy());
		dbDesignation.setModifiedByEmailId(designation.getModifiedByEmailId());
		dbDesignation.setModifiedDateTime(LocalDateTime.now());
		Designation updatedDesignation = designationRepository.save(dbDesignation);
		return updatedDesignation;
	}

	@Override
	public void deleteDesignationById(Long id) {
		Optional<Designation> optionalDbDesignation = designationRepository.findById(id);
		Designation designationToBeDeleted = optionalDbDesignation.orElseThrow(() ->
		new EntityNotFoundException());
		designationRepository.delete(designationToBeDeleted);
	}

	@Override
	public Designation getDesignationById(Long id) {
		Optional<Designation> optionalDbDesignation = designationRepository.findById(id);
		Designation designation = null;
		if(optionalDbDesignation.isPresent()) {
			designation = optionalDbDesignation.orElseThrow(()-> 
			new EntityNotFoundException());
		}
		return designation;
	}

	@Override
	public List<Designation> getAllDesignations() {
		List<Designation> designationList = designationRepository.findAll();
		return designationList;
	}

	@Transactional
	@Override
	public void deleteSelectedDesignationsByIds(List<Long> ids) {
		if(ids.size() < 1) {
			throw new EmptyListException(ErrorCodeMessages.ERR_DESG_IDS_LIST_IS_EMPTY_CODE, 
					ErrorCodeMessages.ERR_DESG_IDS_LIST_IS_EMPTY_MSG);
		}
		List<Designation> designationList = designationRepository.findAllById(ids);
		if(designationList.size() > 0) {
			designationRepository.deleteAll(designationList);
		}
		
	}

}
