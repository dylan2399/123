package com.nt.rookies.asset.exception;


import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
@Getter
@Setter
public class CategoryException extends RuntimeException{

    private CodeResponse codeResponse;

    public CategoryException(CodeResponse codeResponse){
        this.codeResponse = codeResponse;
    }

    public static final CodeResponse SUCCESS =
            new CodeResponse("SUCCESS", "SUCCESS", HttpStatus.OK);

    public static final CodeResponse ERR_RETRIEVE_CATEGORY_FAIL =
            new CodeResponse("CATEGORY-01", "Failed to retrieve category", HttpStatus.INTERNAL_SERVER_ERROR);

    public static final CodeResponse ERR_CONVERT_DTO_ENTITY_FAIL
            =  new CodeResponse("CATEGORY-02", "Failed to convert category", HttpStatus.INTERNAL_SERVER_ERROR);


    public static final CodeResponse CATEGORY_NAME_UNIQUE
            = new CodeResponse("CA-01", "Category name must be unique!", HttpStatus.BAD_REQUEST);

    public static final CodeResponse CATEGORY_PREFIX_UNIQUE
            = new CodeResponse("CA-05", "Category prefix must be unique!", HttpStatus.BAD_REQUEST);

    public static final CodeResponse CATEGORY_CREATE_FAIL
            = new CodeResponse("CA-04", "Create category fail", HttpStatus.BAD_REQUEST);

    public static final CodeResponse CATEGORY_NOT_FOUND
            = new CodeResponse("CATEGORY-03", "Category not found", HttpStatus.NOT_FOUND);

}
