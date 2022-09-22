package com.nt.rookies.asset.exception;

import lombok.Getter;
import lombok.Setter;

import org.springframework.http.HttpStatus;

@Getter
@Setter
public class RequestException extends RuntimeException{

	public static final CodeResponse REQUEST_NOT_FOUND =
			new CodeResponse("RE-01", "Request not found", HttpStatus.NOT_FOUND);

	public static final CodeResponse CREATED_REQUEST_FAIL =
			new CodeResponse("RE-02", "Create request fail", HttpStatus.NOT_FOUND);

	public static final CodeResponse ERR_CONVERT_DTO_ENTITY_FAIL = new CodeResponse("RE-03", "Convert request fail",
			HttpStatus.INTERNAL_SERVER_ERROR);

	public static final CodeResponse STATE_IS_WRONG = new CodeResponse("RE-04", "State is wrong!",
			HttpStatus.INTERNAL_SERVER_ERROR);

	private CodeResponse codeResponse;


	public RequestException(CodeResponse codeResponse) {
		this.codeResponse = codeResponse;
	}
}
