package com.nt.rookies.asset.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Getter
@Setter
public class UserEditDTO {
	private String staffCode;
	private String firstName;
	private String lastName;
	@NotBlank(message = "Date of birth can not be empty")
	private String dob;

	@NotBlank(message = "Joined date can not be empty")
	private String joinedDate;

	private String gender;

	@NotBlank(message = "Type can not be empty")
	private String type;

	private String updatedBy;
}
