package com.ikn.ums.employee.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employee_master")
public class Employee {

	// user login properties
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "Id")
	private Integer id;

	@Column(name = "teams_user_id", nullable = true)
	private String teamsUserId;

	@Column(name = "first_name", nullable = false)
	private String firstName;

	@Column(name = "last_name", nullable = true)
	private String lastName;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

//	@Column(name = "encrypted_password", nullable = false, unique = false)
//	private String encryptedPassword;

//	@Column(name = "role", nullable = false, unique = false)
//	private String userRole;

	// @Column(name = "UserId")
	// private String userId;
	//@Column(name = "department", nullable = false, unique = false)
	//private String department;

	@Column(name = "designation", nullable = true, unique = false)
	private String designation;

//	@Column(name = "otp_code", nullable = true, unique = false)
//	private int otpCode;
//
//	@Column(name = "two_factor_authentication", nullable = true, unique = false)
//	private boolean twoFactorAuthentication;
	
	@Column(name = "department_id", nullable = false, unique = false)
	private Long departmentId;
}
