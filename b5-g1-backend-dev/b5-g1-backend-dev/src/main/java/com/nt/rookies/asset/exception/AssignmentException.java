package com.nt.rookies.asset.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignmentException extends RuntimeException {
    private CodeResponse codeResponse;

    public static final CodeResponse ERR_CONVERT_DTO_ENTITY_FAIL = new CodeResponse("ASSIGNMENT-01",
            "Convert assignment failed", HttpStatus.INTERNAL_SERVER_ERROR);

    public static final CodeResponse LIST_NOT_FOUND
            = new CodeResponse("ASSIGN-01", "List not found", HttpStatus.NOT_FOUND);

    public static final CodeResponse ASSIGN_STATE_WRONG
            = new CodeResponse("ASSIGN-02", "Assignment state is wrong", HttpStatus.BAD_REQUEST);

    public static final CodeResponse ASSIGN_NOT_FOUND
            = new CodeResponse("ASSIGN-03", "Assignment not found", HttpStatus.NOT_FOUND);

    public static final CodeResponse ASSIGN_UPDATE_FAIL
            = new CodeResponse("ASSIGN-04", "Update assignment fail", HttpStatus.BAD_REQUEST);

    public static final CodeResponse ASSIGN_RESPOND_FAIL
            = new CodeResponse("ASSIGN-05", "Respond to assignment fail", HttpStatus.BAD_REQUEST);

    public AssignmentException(CodeResponse codeResponse) {
        this.codeResponse = codeResponse;
    }

}
