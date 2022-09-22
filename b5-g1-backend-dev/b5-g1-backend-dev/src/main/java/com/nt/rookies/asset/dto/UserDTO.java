package com.nt.rookies.asset.dto;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Data
@Getter
@Setter
@ToString
public class UserDTO {

	private String staffCode;

	private String status;

	@NotBlank(message = "Please enter First Name")
	@Length(min = 1, max = 50, message = "The name length should be 1 - 50 characters")
	@Pattern(regexp = "^[A-Za-z\\s]+$", message = "Name should not contain numbers and special characters")
	private String firstName;

	@NotBlank(message = "Please enter Last Name")
	@Length(min = 1, max = 50, message = "The name length should be 1 - 50 characters")
	@Pattern(regexp = "^[A-Za-z\\s]+$", message = "Name  should not contain numbers and special characters")
	private String lastName;

	private String username;

	@NotBlank(message = "Date of birth can not be empty")
	private String dob;

	@NotBlank(message = "Joined date can not be empty")
	private String joinedDate;

	private String gender;

	@NotBlank(message = "Type can not be empty")
	private String type;

	private String location;

	private boolean isFirstLogin;

	private String createdBy;

	private String updatedBy;
}
