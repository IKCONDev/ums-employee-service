package com.ikn.ums.employee.VO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamVO {

    private Long teamId;	
    private String teamName;
    private String teamCode;
    private DepartmentVO department;
    private String teamHead;
    private String teamHeadFullName;
    private String active; // true: active, false: inactive
	private String createdBy;
	private String createdByEmailId;
	private LocalDateTime createdDateTime;
	private String modifiedBy;
	private String modifiedByEmailId;
	private LocalDateTime modifiedDateTime;
	
}
