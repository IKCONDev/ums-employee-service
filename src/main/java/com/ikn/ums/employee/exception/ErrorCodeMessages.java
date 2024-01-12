package com.ikn.ums.employee.exception;

public class ErrorCodeMessages {

	 public static final String ERR_EMP_SERVICE_NOT_FOUND_CODE = "EMPLOYEE-CORE-SERVICE-1001";
	 public static final String ERR_EMP_SERVICE_NOT_FOUND_MSG = "Requested Employee Service not present.";
	 
	 public static final String ERR_EMP_ENTITY_IS_NULL_CODE = "EMPLOYEE-ENTITY-IS-NULL-1002";
	 public static final String ERR_EMP_ENTITY_IS_NULL_MSG = "Employee Entity is Null."; 

	 public static final String ERR_EMP_ID_NOT_FOUND_CODE = "EMPLOYEE-ID-NOT-FOUND-1003";
	 public static final String ERR_EMP_ID_NOT_FOUND_MSG = "Requested Employee Id is not present."; 

	 public static final String ERR_DEPT_SERVICE_NOT_FOUND_CODE = "DEPARTMENT-CORE-SERVICE-1004";
	 public static final String ERR_DEPT_SERVICE_NOT_FOUND_MSG = "Requested Department Service is not present."; 
	 
	 public static final String ERR_DEPT_ID_NOT_FOUND_CODE = "DEPARTMENT-ID-NOT-FOUND-1005";
	 public static final String ERR_DEPT_ID_NOT_FOUND_MSG = "Requested Department Id is not present."; 

	 public static final String ERR_EMP_ID_ALREADY_EXISTS_CODE = "EMPLOYEE-ID-ALREADY-EXISTS-1006";
	 public static final String ERR_EMP_ID_ALREADY_EXISTS_MSG = "Employee Id Already Exists."; 

	 public static final String ERR_EMP_FIRST_NAME_IS_NULL_CODE = "EMPLOYEE_FIRST_NAME_IS_NULL-1007";
	 public static final String ERR_EMP_FIRST_NAME_IS_NULL_MSG = "Employee First Name Is Null."; 

	 public static final String ERR_EMP_LAST_NAME_IS_NULL_CODE = "EMPLOYEE_LAST_NAME_IS_NULL-1008";
	 public static final String ERR_EMP_LAST_NAME_IS_NULL_MSG = "Employee Last Name Is Null."; 
	 
	 public static final String ERR_EMP_SAVE_SUCCESS_CODE = "EMPLOYEE-SAVE-SUCCESS-1009";
	 public static final String ERR_EMP_SAVE_SUCCESS_MSG = "Employee Save Sucessfull."; 	 
	 
	 public static final String ERR_EMP_LIST_IS_EMPTY_CODE = "EMPLOYEE_LIST_IS_EMPTY-1010";
	 public static final String ERR_EMP_LIST_IS_EMPTY_MSG = "Employee List Is Empty."; 	 

	 public static final String ERR_EMP_SERVICE_EXCEPTION_CODE = "EMPLOYEE_SERVICE_EXCEPTION_CODE-1011";
	 public static final String ERR_EMP_SERVICE_EXCEPTION_MSG = "Exception Occured in the Employee Service Layer."; 	

	 public static final String ERR_EMP_EMAIL_ID_NOT_FOUND_CODE = "EMPLOYEE_EMAIL_ID_NOT_FOUND_CODE-1012";
	 public static final String ERR_EMP_EMAIL_ID_NOT_FOUND_MSG = "Employee Email Id is not found."; 	
	 
	 public static final String ERR_EMP_SAVE_UNSUCCESS_CODE = "EMPLOYEE-SAVE-UNSUCCESS-1013";
	 public static final String ERR_EMP_SAVE_UNSUCCESS_MSG = "Error Occured While Saving Employee."; 	 

	 public static final String ERR_EMP_UPDATE_UNSUCCESS_CODE = "EMPLOYEE-UPDATE-UNSUCCESS-1014";
	 public static final String ERR_EMP_UPDATE_UNSUCCESS_MSG = "Error Occured While Updating Employee."; 	

	 public static final String ERR_EMP_DELETE_UNSUCCESS_CODE = "EMPLOYEE-DELETE-UNSUCCESS-1015";
	 public static final String ERR_EMP_DELETE_UNSUCCESS_MSG = "Error Occured While Deleting Employee."; 	
	 
	 public static final String ERR_EMP_EXISTS_EXCEPTION_CODE = "EMPLOYEE-ALREADY-EXISTS-1016";
	 public static final String ERR_EMP_EXISTS_EXCEPTION_MSG = "Employee Already Exists !"; 	

	 public static final String ERR_EMP_DETAILS_GET_UNSUCESS_CODE = "EMPLOYEE-DETAILS-GET-UNSUCESS-1017";
	 public static final String ERR_EMP_DETAILS_GET_UNSUCESS_MSG = "Error Occured While Retrieving Employee Details !"; 	
	 
	 public static final String ERR_EMP_DEPT_REST_CLIENT_EXCEPTION_CODE = "DEPARTMENT-REST-CLIENT-EXCEPTION-1018";
	 public static final String ERR_EMP_DEPT_REST_CLIENT_EXCEPTION_MSG = "Error Occured While Calling Department Microservice !";
	 
	 public static final String ERR_EMP_IDS_LIST_IS_EMPTY_CODE = "ERR_EMP_IDS_LIST_IS_EMPTY_CODE-1019";
	 public static final String ERR_EMP_IDS_LIST_IS_EMPTY_MSG = "Employee ids list is empty !";
	 
	 public static final String ERR_EMP_ENTITY_NOT_FOUND_CODE = "ERR_EMP_ENTITY_NOT_FOUND_CODE-1020";
	 public static final String ERR_EMP_ENTITY_NOT_FOUND_MSG = "Requested Employee entity is not present."; 
	 
	 public static final String ERR_EMP_DBENTITY_NOT_FOUND_CODE = "ERR_EMP_DBENTITY_NOT_FOUND_CODE-1009";
	 public static final String ERR_EMP_DBENTITY_NOT_FOUND_MSG = "Employee not present in DB.";
	 
	 public static final String ERR_EMP_EMPORGID_NOT_FOUND_CODE = "ERR_EMP_EMPORGID_NOT_FOUND_CODE-1010";
	 public static final String ERR_EMP_EMPORGID_NOT_FOUND_MSG = "Employee OrgId is empty.";
	 
	 //DESIGNATIONS
	 public static final String ERR_DESG_IDS_LIST_IS_EMPTY_CODE = "ERR_DESG_IDS_LIST_IS_EMPTY_CODE-1001";
	 public static final String ERR_DESG_IDS_LIST_IS_EMPTY_MSG = "Designation ids list is empty";
	 
	 public static final String ERR_DESG_LIST_DELETE_UNSUCCESS_CODE = "ERR_DESG_LIST_DELETE_UNSUCCESS_CODE-1002";
	 public static final String ERR_DESG_LIST_DELETE_UNSUCCESS_MSG = "Error while deleting designations";
	 
	 public static final String ERR_DESG_ENTITY_IS_NULL_CODE = "DESIGNATION-ENTITY-IS-NULL-1003";
	 public static final String ERR_DESG_ENTITY_IS_NULL_MSG = "Designation Entity is Null."; 
	 
	 public static final String ERR_DESG_NAME_EXISTS_CODE = "ERR_DESG_NAME_EXISTS_CODE-1004";
	 public static final String ERR_DESG_NAME_EXISTS_MSG = "Designation name already exists"; 
	 
	 public static final String ERR_DESG_CREATE_UNSUCCESS_CODE = "DESG_SAVE_UNSUCCESS_CODE-1005";
	 public static final String ERR_DESG_CREATE_UNSUCCESS_MSG = "Exceeption while creating designation";
	 
	 public static final String ERR_DESG_UPDATE_UNSUCCESS_CODE = "DESIGNATION_UPDATE_UNSUCCESS-1006";
	 public static final String ERR_DESG_UPDATE_UNSUCCESS_MSG = "Error Occured While Updating designation.";
	 
	 public static final String ERR_DESG_DETAILS_GET_UNSUCESS_CODE = "DESIGNATION_DETAILS_GET-UNSUCESS-1007";
	 public static final String ERR_DESG_DETAILS_GET_UNSUCESS_MSG = "Error Occured While Retrieving Designation Details !";
	 
	 public static final String ERR_DESG_ID_IS_EMPTY_CODE = "ERR_DESG_ID_IS_EMPTY_CODE-1008";
	 public static final String ERR_DESG_ID_IS_EMPTY_MSG = "Designation id is empty";
	 

	 
}
