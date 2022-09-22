package com.nt.rookies.asset.exception;

import lombok.Getter;
import lombok.Setter;

import org.springframework.http.HttpStatus;

@Getter
@Setter
public class CodeResponse {
	private String code;

	private String message;

	private HttpStatus status;

	public CodeResponse(String code, String message, HttpStatus status) {
		this.code = code;
		this.message = message;
		this.status = status;
	}
}
