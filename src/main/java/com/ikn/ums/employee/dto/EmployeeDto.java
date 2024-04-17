package com.ikn.ums.employee.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDto {
	
	private Integer id;
	
	private String employeeOrgId;

	private String teamsUserId;

	private String firstName;

	private String lastName;

	private String email;
	
	private String reportingManager;
	
	private String designation;

	private DesignationDto empDesignation;
	
	private Long departmentId;
	
	private String gender;
	
	private LocalDateTime createdDateTime;
	
	private LocalDateTime modifiedDateTime;
	
	private String createdBy;
	
	private String modifiedBy;
	
	private String createdByEmailId;
	
	private String modifiedByEmailId;
	
	private boolean isUser;
	
	private String dateOfJoining;
	
	private String encryptedPassword;
	
	private String employeeStatus;

	private boolean enableBatchProcessing;
	
	private String batchProcessStatus;
	
	private DepartmentDto department;

}
