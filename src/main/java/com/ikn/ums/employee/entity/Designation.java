package com.ikn.ums.employee.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "designation_tab")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Designation {
	
	@Id
	@SequenceGenerator(name = "designation_id_gen", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "designation_id_gen")
	private Long id;
	
	@Column(name = "designationName")
	private String designationName;
	
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
