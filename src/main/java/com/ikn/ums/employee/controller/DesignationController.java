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

import com.ikn.ums.employee.entity.Designation;
import com.ikn.ums.employee.exception.ControllerException;
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
	public ResponseEntity<?> createDesignation(@RequestBody Designation designation){
		log.info("DesignationController.createDesignation() is enetered with args - designation");
		if (designation == null) {
			log.info("DesignationController.createDesignation() : designation Object is NULL !");
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_DESG_ENTITY_IS_NULL_CODE,
					ErrorCodeMessages.ERR_DESG_ENTITY_IS_NULL_MSG);
		}
		try {
			log.info("DesignationController.createDesignation() is under execution...");
			Designation savedDesignation =  designationService.createDesignation(designation);
			log.info("DesignationController.createDesignation() executed successfully");
			return new ResponseEntity<>(savedDesignation, HttpStatus.CREATED);
		}
		catch (DesignationNameExistsException | EntityNotFoundException businessException) {
			throw businessException;
		}
		catch (Exception e) {
			log.error("DepartmentController.saveDepartment() : Exception Occured !" + e.fillInStackTrace(),e);
			throw new ControllerException(ErrorCodeMessages.DESG_CREATE_UNSUCCESS_CODE,
					ErrorCodeMessages.DESG_CREATE_UNSUCCESS_MSG);
		}
	}
	@PutMapping("/update")
	public ResponseEntity<?> updateDesignation(@RequestBody Designation designation){
		log.info("DesignationController.updateDesignation() is enetered with args - designation");
		try {
			log.info("DesignationController.updateDesignation() is under execution...");
			Designation updatedDesignation = designationService.updateDesignation(designation);
			log.info("DesignationController.updateDesignation() is executed  successfully");
			return new ResponseEntity<>(updatedDesignation, HttpStatus.PARTIAL_CONTENT);
		}catch (Exception e) {
			log.error("DesignationController.updateDesignation() is exited with exception :"+ e.getMessage(),e);
			throw new ControllerException(ErrorCodeMessages.ERR_DESG_UPDATE_UNSUCCESS_CODE,
					 ErrorCodeMessages.ERR_DESG_UPDATE_UNSUCCESS_MSG);
		}
	
	}
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteDesignation(@PathVariable Long id){
		log.info("DesignationController.deleteDesignation() is enetered with departmentId:"+ id);
		log.info("DesignationController.deleteDesignation() is under execution...");
		boolean isdeleted = false;
		try {
			designationService.deleteDesignationById(id);
			isdeleted = true;
			log.info("DesignationController.deleteDesignation() executed successfully");
			return new ResponseEntity<>(isdeleted, HttpStatus.OK);
		}catch (Exception e) {
			log.error("DesignationController.deleteDesignationById() is exited with exception :"+ e.getMessage(),e);
			throw new ControllerException(ErrorCodeMessages.ERR_DESG_LIST_DELETE_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_DESG_LIST_DELETE_UNSUCCESS_MSG);
		}
		
	}
	@GetMapping("/get/{id}")
	public ResponseEntity<?> getDesignation(@PathVariable Long id){
		log.info("DesignationController.getDesignation() is enetered with departmentId:"+ id);
		log.info("DesignationController.getDesignation() is under execution...");
		try {
			Designation designation = designationService.getDesignationById(id);
			log.info("DesignationController.getDesignation() executed successfully");
			return new ResponseEntity<>(designation, HttpStatus.OK);
		}catch (Exception e) {
			log.error("DesignationController.getDesignation() is exited with exception :"+ e.getMessage(),e);
			throw new ControllerException(ErrorCodeMessages.ERR_DESG_DETAILS_GET_UNSUCESS_CODE,
					ErrorCodeMessages.ERR_DESG_DETAILS_GET_UNSUCESS_MSG);
		}
		
	}
	@GetMapping("/all")
	public ResponseEntity<?> getAllDesignations(){
		log.info("DesignationController.getAllDesignations() is enetered");
		log.info("DesignationController.getAllDesignations() is under execution...");
		try {
			 List<Designation> deisgnationList = designationService.getAllDesignations();
			 log.info("DesignationController.getDesignation() executed successfully");
			 return new ResponseEntity<>(deisgnationList, HttpStatus.OK);
		}catch (Exception e) {
			log.error("DesignationController.getAllDesignations() is exited with exception :"+ e.getMessage(),e);
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
	public ResponseEntity<?> deleteAllDepartmentsByIds(@PathVariable List<Long> ids){
		log.info("DesignationController.deleteAllDepartmentsByIds() is enetered");
		log.info("DesignationController.deleteAllDepartmentsByIds() is under execution...");
		if(ids == null || ids.size() == 0 || ids.equals((null))){
			throw new EmptyInputException(ErrorCodeMessages.ERR_DESG_IDS_LIST_IS_EMPTY_CODE, 
					ErrorCodeMessages.ERR_DESG_IDS_LIST_IS_EMPTY_MSG);
		}
		try {
			designationService.deleteSelectedDesignationsByIds(ids);
			log.info("DesignationController.deleteAllDepartmentsByIds() executed successfully");
			return new ResponseEntity<>(true, HttpStatus.OK);
		}catch (Exception e) {
			log.error("DesignationController.deleteAllDepartmentsByIds() is exited with exception :"+ e.getMessage(),e);
			throw new ControllerException(ErrorCodeMessages.ERR_DESG_LIST_DELETE_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_DESG_LIST_DELETE_UNSUCCESS_MSG);
		}
	}


}
