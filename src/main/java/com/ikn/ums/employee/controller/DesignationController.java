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

import com.ikn.ums.employee.dto.DesignationDto;
import com.ikn.ums.employee.entity.Designation;
import com.ikn.ums.employee.exception.ControllerException;
import com.ikn.ums.employee.exception.DesignationInUsageException;
import com.ikn.ums.employee.exception.DesignationNameExistsException;
import com.ikn.ums.employee.exception.EmptyInputException;
import com.ikn.ums.employee.exception.EntityNotFoundException;
import com.ikn.ums.employee.exception.ErrorCodeMessages;
import com.ikn.ums.employee.service.DesignationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/designations")
public class DesignationController {
	
	@Autowired
	private DesignationService designationService;
	
	@PostMapping("/create")
	public ResponseEntity<DesignationDto> createDesignation(@RequestBody DesignationDto designation){
		log.info("createDesignation() is enetered with args - designation");
		if (designation == null) {
			log.info("createDesignation() : designation Object is NULL !");
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_DESG_ENTITY_IS_NULL_CODE,
					ErrorCodeMessages.ERR_DESG_ENTITY_IS_NULL_MSG);
		}
		try {
			log.info("createDesignation() is under execution...");
			var savedDesignationDto =  designationService.createDesignation(designation);
			log.info("createDesignation() executed successfully");
			return new ResponseEntity<>(savedDesignationDto, HttpStatus.CREATED);
		}
		catch (DesignationNameExistsException | EntityNotFoundException businessException) {
			throw businessException;
		}
		catch (Exception e) {
			log.error("DepartmentController.saveDepartment() : Exception Occured !" + e.fillInStackTrace(),e);
			throw new ControllerException(ErrorCodeMessages.ERR_DESG_CREATE_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_DESG_CREATE_UNSUCCESS_MSG);
		}
	}
	@PutMapping("/update")
	public ResponseEntity<DesignationDto> updateDesignation(@RequestBody DesignationDto designation){
		log.info("updateDesignation() is enetered with args - designation");
		if(designation == null) {
			log.info("updateDesignation() EntityNotFoundException : designation object is null ");
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_DESG_ENTITY_IS_NULL_CODE,
					ErrorCodeMessages.ERR_DESG_ENTITY_IS_NULL_MSG);
		}
		try {
			log.info("updateDesignation() is under execution...");
			var updatedDesignation = designationService.updateDesignation(designation);
			log.info("updateDesignation() is executed  successfully");
			return new ResponseEntity<>(updatedDesignation, HttpStatus.PARTIAL_CONTENT);
		}catch (EntityNotFoundException businesException) {
			log.error("updateDesignation() exited with exception :Business Exception occured while updating designation. "+businesException.getMessage(), businesException);
			throw businesException;
		}
		catch (Exception e) {
			log.error("updateDesignation() is exited with exception :"+ e.getMessage(),e);
			throw new ControllerException(ErrorCodeMessages.ERR_DESG_UPDATE_UNSUCCESS_CODE,
					 ErrorCodeMessages.ERR_DESG_UPDATE_UNSUCCESS_MSG);
		}
	
	}
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Boolean> deleteDesignation(@PathVariable Long id){
		log.info("deleteDesignation() is enetered with departmentId:"+ id);
		if(id <= 0 || id == null) {
			log.info("deleteDesignation() EntityNotFoundException : designation id is null ");
			throw new EmptyInputException(ErrorCodeMessages.ERR_DESG_ID_IS_EMPTY_CODE,
					ErrorCodeMessages.ERR_DESG_ID_IS_EMPTY_MSG);
		
		}
		log.info("deleteDesignation() is under execution...");
		try {
			designationService.deleteDesignationById(id);
			log.info("deleteDesignation() executed successfully");
			return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
		}catch (DesignationInUsageException businessException) {
			log.error("deleteAllDepartmentsByIds() is exited with exception :"+ businessException.getMessage(),businessException);
			throw businessException;
		}catch (Exception e) {
			log.error("deleteDesignationById() is exited with exception :"+ e.getMessage(),e);
			throw new ControllerException(ErrorCodeMessages.ERR_DESG_LIST_DELETE_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_DESG_LIST_DELETE_UNSUCCESS_MSG);
		}
		
	}
	@GetMapping("/get/{id}")
	public ResponseEntity<Designation> getDesignation(@PathVariable Long id){
		log.info("getDesignation() is enetered with departmentId:"+ id);
		if(id <= 0 || id == null) {
			log.info("getDesignation() EntityNotFoundException : designation id is null ");
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_DESG_ENTITY_IS_NULL_CODE,
					ErrorCodeMessages.ERR_DESG_ENTITY_IS_NULL_MSG);
		}
		log.info("getDesignation() is under execution...");
		try {
			var designation = designationService.getDesignationById(id);
			log.info("getDesignation() executed successfully");
			return new ResponseEntity<>(designation, HttpStatus.OK);
		}catch (Exception e) {
			log.error("getDesignation() is exited with exception :"+ e.getMessage(),e);
			throw new ControllerException(ErrorCodeMessages.ERR_DESG_DETAILS_GET_UNSUCESS_CODE,
					ErrorCodeMessages.ERR_DESG_DETAILS_GET_UNSUCESS_MSG);
		}
		
	}
	@GetMapping("/all")
	public ResponseEntity<List<Designation>> getAllDesignations(){
		log.info("getAllDesignations() is enetered");
		log.info("getAllDesignations() is under execution...");
		try {
			 var deisgnationList = designationService.getAllDesignations();
			 log.info("getDesignation() executed successfully");
			 return new ResponseEntity<>(deisgnationList, HttpStatus.OK);
		}catch (Exception e) {
			log.error("getAllDesignations() is exited with exception : "+ e.getMessage(),e);
			throw new ControllerException(ErrorCodeMessages.ERR_DESG_DETAILS_GET_UNSUCESS_CODE,
					ErrorCodeMessages.ERR_DESG_DETAILS_GET_UNSUCESS_MSG);
		}
	   
	}
	
	/**
	 * 
	 * @param ids
	 * @return
	 */
	@DeleteMapping("/delete/all/{ids}")
	public ResponseEntity<Boolean> deleteAllDepartmentsByIds(@PathVariable List<Long> ids){
		log.info("deleteAllDepartmentsByIds() is enetered");
		log.info("deleteAllDepartmentsByIds() is under execution...");
		if(ids == null || ids.isEmpty()){
			throw new EmptyInputException(ErrorCodeMessages.ERR_DESG_IDS_LIST_IS_EMPTY_CODE, 
					ErrorCodeMessages.ERR_DESG_IDS_LIST_IS_EMPTY_MSG);
		}
		try {
			designationService.deleteSelectedDesignationsByIds(ids);
			log.info("deleteAllDepartmentsByIds() executed successfully");
			return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
		}catch (DesignationInUsageException businessException) {
			log.error("deleteAllDepartmentsByIds() is exited with exception : "+ businessException.getMessage(),businessException);
			throw businessException;
		}
		catch (Exception e) {
			log.error("deleteAllDepartmentsByIds() is exited with exception : "+ e.getMessage(),e);
			throw new ControllerException(ErrorCodeMessages.ERR_DESG_LIST_DELETE_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_DESG_LIST_DELETE_UNSUCCESS_MSG);
		}
	}


}
