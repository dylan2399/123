package com.nt.rookies.asset.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ViewReturnRequestDTO {

	private Integer requestId;

	private String requestBy;

	private String acceptBy;

	private String returnDate;

	private String state;

	private Integer assignmentId;

	private String assetCode;

	private String assetName;

	private String assignDateString;

}
