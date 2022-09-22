package com.nt.rookies.asset.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


import com.nt.rookies.asset.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;

@RestControllerAdvice
@AllArgsConstructor
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(AppExceptionHandler.class);

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    var apiError = Objects.isNull(body) ? new ApiError(status, exception.getMessage()) // <--
	: body;
    return super.handleExceptionInternal(exception, apiError, headers, status, request);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity handleNotFoundException(NotFoundException ex, WebRequest request) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("message", ex.getMessage());
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDate.now());
    body.put("status", status.value());
    List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(x -> x.getDefaultMessage())
	.collect(Collectors.toList());
    body.put("errors", errors);
    logger.warn(errors.toString());
    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UserException.class)
  public ResponseEntity handleUrlException(UserException userException) {
    logger.error(userException.getCodeResponse().getMessage());
    return new ResponseEntity<>(userException.getCodeResponse().getMessage(),
	userException.getCodeResponse().getStatus());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity globalExceptionHandler(Exception ex, WebRequest request) {
    logger.error(""+ex);
    return new ResponseEntity<>("Error: " + ex, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity illegalArgumentExceptionHandler(IllegalArgumentException ex, WebRequest request) {
    logger.error(ex.getMessage());
    return new ResponseEntity<>("Error: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseEntity expiredJwtExceptionHandler(ExpiredJwtException ex, WebRequest request) {
    logger.error(ex.getMessage());
    return new ResponseEntity<>("Error: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(AssetException.class)
  public ResponseEntity handleAssetException(AssetException assetException) {
    logger.error(assetException.getCodeResponse().getMessage());
    return new ResponseEntity<>(assetException.getCodeResponse().getMessage(),
	assetException.getCodeResponse().getStatus());
  }

  @ExceptionHandler(AssignmentException.class)
  public ResponseEntity handleAssignException(AssignmentException assignmentException) {
    logger.error(assignmentException.getCodeResponse().getMessage());
    return new ResponseEntity<>(assignmentException.getCodeResponse().getMessage(),
            assignmentException.getCodeResponse().getStatus());
  }

  @ExceptionHandler(CategoryException.class)
  public ResponseEntity handleCategoryException(CategoryException categoryException) {
    logger.error(categoryException.getCodeResponse().getMessage());
    return new ResponseEntity<>(categoryException.getCodeResponse().getMessage(),
            categoryException.getCodeResponse().getStatus());
  }

}
