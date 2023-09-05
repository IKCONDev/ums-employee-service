package com.ikn.ums.employee.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ikn.ums.employee.VO.TeamsUserProfileVO;

import lombok.Data;

@Data
public class UserProfilesResponseWrapper {
	
	@JsonProperty("odata.context")
	private String odataContext;
	
	private List<TeamsUserProfileVO> value;


}
