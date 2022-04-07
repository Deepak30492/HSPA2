package com.uhi.hsp.customexception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class CustomExceptionHandler {
	@ExceptionHandler(RecordNotFoundException.class)
	public ResponseEntity<Error> recordNotFound(RecordNotFoundException exception,WebRequest request){
		Error error=new Error();
		error.setMessage(exception.getMessage());
		error.setCode(HttpStatus.NOT_FOUND.toString());
		error.setPath(request.getDescription(false));
		error.setType("Exception");
		return new ResponseEntity<Error>(error,org.springframework.http.HttpStatus.NOT_FOUND);	
	}

}
