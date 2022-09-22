package com.nt.rookies.asset.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReturnRequestDTO {

	Integer requestId;

	String requestBy;

	String acceptBy;

	String returnDate;

	String state;

	Integer assignmentId;

}
