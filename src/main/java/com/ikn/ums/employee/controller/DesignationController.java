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
		if (designation == null) {
			throw new EntityNotFoundException(ErrorCodeMessages.ERR_DESG_ENTITY_IS_NULL_CODE,
					ErrorCodeMessages.ERR_DESG_ENTITY_IS_NULL_MSG);
		}
		try {
			Designation savedDesignation =  designationService.createDesignation(designation);
			return new ResponseEntity<>(savedDesignation, HttpStatus.CREATED);
		}
		catch (DesignationNameExistsException | EntityNotFoundException businessException) {
			throw businessException;
		}
		catch (Exception e) {
			log.info("DepartmentController.saveDepartment() : Exception Occured !" + e.fillInStackTrace());
			throw new ControllerException(ErrorCodeMessages.DESG_CREATE_UNSUCCESS_CODE,
					ErrorCodeMessages.DESG_CREATE_UNSUCCESS_MSG);
		}
	}
	@PutMapping("/update")
	public ResponseEntity<?> updateDesignation(@RequestBody Designation designation){
		Designation updatedDesignation = designationService.updateDesignation(designation);
		return new ResponseEntity<>(updatedDesignation, HttpStatus.PARTIAL_CONTENT);
	}
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteDesignation(@PathVariable Long id){
		boolean isdeleted = false;
		designationService.deleteDesignationById(id);
		isdeleted = true;
		return new ResponseEntity<>(isdeleted, HttpStatus.OK);
	}
	@GetMapping("/get/{id}")
	public ResponseEntity<?> getDesignation(@PathVariable Long id){
		Designation designation = designationService.getDesignationById(id);
		return new ResponseEntity<>(designation, HttpStatus.OK);
	}
	@GetMapping("/all")
	public ResponseEntity<?> getAllDesignations(){
	    List<Designation> deisgnationList = designationService.getAllDesignations();
	    return new ResponseEntity<>(deisgnationList, HttpStatus.OK);
	}
	
	/**
	 * 
	 * @param ids
	 * @return
	 */
	@DeleteMapping("/delete/all/{ids}")
	public ResponseEntity<?> deleteAllDepartmentsByIds(@PathVariable List<Long> ids){
		if(ids == null || ids.size() == 0 || ids.equals((null))){
			throw new EmptyInputException(ErrorCodeMessages.ERR_DESG_IDS_LIST_IS_EMPTY_CODE, 
					ErrorCodeMessages.ERR_DESG_IDS_LIST_IS_EMPTY_MSG);
		}
		try {
			designationService.deleteSelectedDesignationsByIds(ids);
			return new ResponseEntity<>(true, HttpStatus.OK);
		}catch (Exception e) {
			throw new ControllerException(ErrorCodeMessages.ERR_DESG_LIST_DELETE_UNSUCCESS_CODE,
					ErrorCodeMessages.ERR_DESG_LIST_DELETE_UNSUCCESS_MSG);
		}
	}


}
