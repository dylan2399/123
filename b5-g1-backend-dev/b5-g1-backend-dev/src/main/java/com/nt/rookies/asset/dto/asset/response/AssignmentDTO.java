package com.nt.rookies.asset.dto.asset.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AssignmentDTO {

	private Integer id;

	private String assetCode;

	private String assetName;

	private String specification;

	private String assignTo;

	private String assignBy;

	private String assignToUsername;

	private String assignByUsername;

	private LocalDateTime assignDate;

	private String note;

	private String state;

	private Boolean isDeleted;

	private String assignDateString;
	
	private String createdDateString;

    public AssignmentDTO(Integer id, String assignTo, String assignBy, LocalDateTime assignDate, String note,
            String state, Boolean isDeleted) {
        super();
        this.id = id;
        this.assignTo = assignTo;
        this.assignBy = assignBy;
        this.assignDate = assignDate;
        this.note = note;
        this.state = state;
        this.isDeleted = isDeleted;
    }
}
