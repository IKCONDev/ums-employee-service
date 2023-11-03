package com.ikn.ums.employee.entity;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employee_tab")
public class Employee {

	// user login properties
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "Id")
	private Integer id;
	
	@Column(name = "employeeOrgId", nullable = true)
	private String employeeOrgId;

	@Column(name = "teamsUserId", nullable = true)
	private String teamsUserId;

	@Column(name = "first_name", nullable = false)
	private String firstName;

	@Column(name = "lastName", nullable = true)
	private String lastName;

	@Column(name = "email", nullable = false, unique = true)
	private String email;
	
	@Column(name = "reportingManager", nullable = true)
	private String reportingManager;

//	@Column(name = "encrypted_password", nullable = false, unique = false)
//	private String encryptedPassword;

//	@Column(name = "role", nullable = false, unique = false)
//	private String userRole;

	// @Column(name = "UserId")
	// private String userId;
	//@Column(name = "department", nullable = false, unique = false)
	//private String department;

	@Column(name = "designation", nullable = true)
	private String designation;
	
	@OneToOne(orphanRemoval = false,optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "designationId",referencedColumnName = "id")
	private Designation empDesignation;

//	@Column(name = "otp_code", nullable = true, unique = false)
//	private int otpCode;
//
//	@Column(name = "two_factor_authentication", nullable = true, unique = false)
//	private boolean twoFactorAuthentication;
	
	@Column(name = "departmentId", nullable = false, unique = false)
	private Long departmentId;
	
	@Column(name = "gender", nullable = false, unique = false)
	private String gender;
	
	@Column(name = "createdDateTime")
	private LocalDateTime createdDateTime;
	
	@Column(name = "modifiedDateTime")
	private LocalDateTime modifiedDateTime;
	
	@Column(name = "createdBy")
	private String createdBy;
	
	@Column(name = "modifiedBy")
	private String modifiedBy;
	
	@Column(name = "createdByEmailId")
	private String createdByEmailId;
	
	@Column(name = "modifiedByEmailId")
	private String modifiedByEmailId;
	
}
	