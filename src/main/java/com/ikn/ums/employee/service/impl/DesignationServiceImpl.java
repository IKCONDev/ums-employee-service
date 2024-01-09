package com.ikn.ums.employee.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ikn.ums.employee.dto.DesignationDto;
import com.ikn.ums.employee.entity.Designation;
import com.ikn.ums.employee.exception.DesignationNameExistsException;
import com.ikn.ums.employee.exception.EmptyInputException;
import com.ikn.ums.employee.exception.EmptyListException;
import com.ikn.ums.employee.exception.EntityNotFoundException;
import com.ikn.ums.employee.exception.ErrorCodeMessages;
import com.ikn.ums.employee.repository.DesignationRepository;
import com.ikn.ums.employee.service.DesignationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DesignationServiceImpl implements DesignationService {
	
	@Autowired
	private DesignationRepository designationRepository;
	@Autowired
	private ModelMapper mapper;

	@Override
	public DesignationDto createDesignation(DesignationDto designation) {
		log.info("createDesignation() is entered with args: designation");
		if (designation == null) {
			log.info("createDesignation(): designation object is null");
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_DESG_ENTITY_IS_NULL_CODE,
					ErrorCodeMessages.ERR_DESG_ENTITY_IS_NULL_MSG);
		}
		if(isDesignationNameExists(designation)) {
			throw new DesignationNameExistsException(ErrorCodeMessages.ERR_DESG_NAME_EXISTS_CODE,
					ErrorCodeMessages.ERR_DESG_NAME_EXISTS_MSG);
		}
		log.info("createDesignation() is under execution...");
		designation.setCreatedDateTime(LocalDateTime.now());
		Designation entity = new Designation();
		mapper.map(designation,entity);
		Designation createdDesignation = designationRepository.save(entity);
		DesignationDto createdDesignationDTO = new DesignationDto();
		mapper.map(createdDesignation,createdDesignationDTO);
		log.info("createDesignation() executed successfully");
		return createdDesignationDTO;
	}

	@Override
	public DesignationDto updateDesignation(DesignationDto designation) {
		log.info("updateDesignation() is entered with args: designation");
		if (designation == null) {
			log.info("updateDesignation(): designation object is null");
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_DESG_ENTITY_IS_NULL_CODE,
					ErrorCodeMessages.ERR_DESG_ENTITY_IS_NULL_MSG);
		}
		Optional<Designation> optionalDbDesignation = designationRepository.findById(designation.getId());
		Designation dbDesignation = null;
		if(optionalDbDesignation.isPresent()) {
			dbDesignation = optionalDbDesignation.get();
		}
		//map required properties to be updated
		log.info("updateDesignation() is under execution...");
		dbDesignation.setDesignationName(designation.getDesignationName());
		dbDesignation.setModifiedBy(designation.getModifiedBy());
		dbDesignation.setModifiedByEmailId(designation.getModifiedByEmailId());
		dbDesignation.setModifiedDateTime(LocalDateTime.now());
		mapper.map(designation, dbDesignation);
		Designation updatedDesignation = designationRepository.save(dbDesignation);
		DesignationDto updatedDesignationDto = new DesignationDto();
		mapper.map(updatedDesignation,updatedDesignationDto);
		log.info("updateDesignation() executed successfully");
		return updatedDesignationDto;
	}

	@Override
	public void deleteDesignationById(Long id) {
		log.info("deleteDesignationById() is entered with args: id - "+ id);
		if(id <= 0 || id == null) {
			log.info("deleteDesignationById() : designation id is null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_DEPT_ID_NOT_FOUND_CODE,
					ErrorCodeMessages.ERR_DEPT_ID_NOT_FOUND_MSG);
		}
		log.info("deleteDesignationById() is under execution...");
		Optional<Designation> optionalDbDesignation = designationRepository.findById(id);
		Designation designationToBeDeleted = optionalDbDesignation.orElseThrow(() ->
		new EntityNotFoundException());
		designationRepository.delete(designationToBeDeleted);
		log.info("deleteDesignationById() executed sucessfully");
	}

	@Override
	public Designation getDesignationById(Long id) {
		log.info("getDesignationById() is entered with args: id - "+id);
		if(id <= 0 || id == null) {
			log.info("getDesignationById() : designation id is null");
			throw new EmptyInputException(ErrorCodeMessages.ERR_DEPT_ID_NOT_FOUND_CODE,
					ErrorCodeMessages.ERR_DEPT_ID_NOT_FOUND_MSG);
		}
		log.info("getDesignationById() is under execution...");
		Optional<Designation> optionalDbDesignation = designationRepository.findById(id);
		Designation designation = null;
		if(optionalDbDesignation.isPresent()) {
			designation = optionalDbDesignation.orElseThrow(()-> 
			new EntityNotFoundException());
		}
		log.info("getDesignationById() executed sucessfully");
		return designation;
	}

	@Override
	public List<Designation> getAllDesignations() {
		log.info("getAllDesignations() is entered");
		log.info("getAllDesignations() is under execution...");
		List<Designation> designationList = designationRepository.findAll();
		log.info("getAllDesignations() executed sucessfully");
		return designationList;
	}

	@Transactional
	@Override
	public void deleteSelectedDesignationsByIds(List<Long> ids) {
		log.info("deleteDesignationById() is entered with args: ids");
		if(ids.size() <= 0) {
			throw new EmptyListException(ErrorCodeMessages.ERR_DESG_IDS_LIST_IS_EMPTY_CODE, 
					ErrorCodeMessages.ERR_DESG_IDS_LIST_IS_EMPTY_MSG);
		}
		log.info("deleteDesignationById() is under execution...");
		List<Designation> designationList = designationRepository.findAllById(ids);
		if(designationList.size() > 0) {
			designationRepository.deleteAll(designationList);
		}
		log.info("deleteDesignationById() executed sucessfully");
	}
	
	private boolean isDesignationNameExists(DesignationDto designation) {
		log.info("isDesignationNameExists() ENTERED : role : " );
		boolean isDesgNameExists = false;
		if (designation == null) {
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_DESG_ENTITY_IS_NULL_CODE,
					ErrorCodeMessages.ERR_DESG_ENTITY_IS_NULL_MSG);
		} else {
			log.info("isDesignationNameExists() is under execution...");
			log.info("DesignationServiceImpl  : Dept Id : " + designation.getId() + " Dept Name : " + designation.getDesignationName());
			Optional<Designation> optRole = designationRepository.findByDesignationName( designation.getDesignationName() );
		//	isRoleNameExists = optRole.get().getRoleName().equalsIgnoreCase(role.getRoleName());
			isDesgNameExists = optRole.isPresent();
			log.info("DesignationServiceImpl  : isDesgNameExists : " + isDesgNameExists);
		}
		log.info("isDesignationNameExists() executed sucessfully");
		return isDesgNameExists;
	}

}
