package com.nt.rookies.asset.dto.assignment;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SearchAssignmentDTO {
	private String state;
	private String assignedDate;
	private String searchKey;

}
