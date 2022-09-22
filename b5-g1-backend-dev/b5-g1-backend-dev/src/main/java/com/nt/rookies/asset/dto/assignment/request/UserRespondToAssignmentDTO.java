package com.nt.rookies.asset.dto.assignment.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserRespondToAssignmentDTO {
	private Integer assignmentID;
	private String action;
}
