package com.proj.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;


@RestControllerAdvice
public class GlobalExceptionHandler {

		public static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
		
		@ExceptionHandler(ResourceNotFoundException.class)
		public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req){
			return build(HttpStatus.NOT_FOUND,ex.getMessage(), req,null);
		}
		
		 @ExceptionHandler(DuplicateEmailException.class)
		    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateEmailException ex, HttpServletRequest req) {
		        return build(HttpStatus.CONFLICT, ex.getMessage(), req, null);
		    }

		    /** Safety net for a race where two requests pass the email check at the same moment. */
		    @ExceptionHandler(DataIntegrityViolationException.class)
		    public ResponseEntity<ErrorResponse> handleIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
		        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
		        return build(HttpStatus.CONFLICT, "Request conflicts with existing data (for example, a duplicate email)", req, null);
		    }

		    @ExceptionHandler(MethodArgumentNotValidException.class)
		    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
		        Map<String, String> fields = new LinkedHashMap<>();
		        ex.getBindingResult().getFieldErrors()
		                .forEach(fe -> fields.putIfAbsent(fe.getField(), fe.getDefaultMessage()));
		        return build(HttpStatus.BAD_REQUEST, "Validation failed", req, fields);
		    }

		    @ExceptionHandler(HttpMessageNotReadableException.class)
		    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
		        return build(HttpStatus.BAD_REQUEST,
		                "Malformed request body or invalid value (check JSON syntax and enum values such as stage)", req, null);
		    }

		    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
		    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
		        return build(HttpStatus.BAD_REQUEST,
		                "Invalid value for parameter '" + ex.getName() + "'", req, null);
		    }

		    @ExceptionHandler(Exception.class)
		    public ResponseEntity<ErrorResponse> handleAny(Exception ex, HttpServletRequest req) {
		        log.error("Unexpected error on {}", req.getRequestURI(), ex);
		        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong on our side", req, null);
		    }
		    
		    @ExceptionHandler(InvalidStageTransitionException.class)
		    public ResponseEntity<ErrorResponse> handleInvalidTransition(InvalidStageTransitionException ex, HttpServletRequest req) {
		        return build(HttpStatus.CONFLICT, ex.getMessage(), req, null);
		    }

		    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message,
		                                                HttpServletRequest req, Map<String, String> fields) {
		        ErrorResponse body = new ErrorResponse(
		                LocalDateTime.now(),
		                status.value(),
		                status.getReasonPhrase(),
		                message,
		                req.getRequestURI(),
		                fields
		        );
		        return ResponseEntity.status(status).body(body);
		    }
		
}
