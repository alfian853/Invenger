package com.bliblifuture.Invenger.controller_advice;

import com.bliblifuture.Invenger.exception.DataNotFoundException;
import com.bliblifuture.Invenger.exception.DefaultException;
import com.bliblifuture.Invenger.exception.DuplicateEntryException;
import com.bliblifuture.Invenger.exception.InvalidRequestException;
import com.bliblifuture.Invenger.response.jsonResponse.RequestResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;


@ControllerAdvice
public class DefaultControllerAdvice {

    @ExceptionHandler(DataNotFoundException.class)
    @RequestMapping(produces = "application/vnd.error+json")
    public ResponseEntity<RequestResponse> DataNotFoundExceptionHandler(DataNotFoundException exception){

//        exception.printStackTrace();
        System.out.println("welcomee");
        RequestResponse response = new RequestResponse();
        response.setStatusToFailed();
        response.setMessage(exception.getMessage());
        HttpHeaders headers = new HttpHeaders();
        HttpStatus status = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(response, headers,status);
    }

    @ExceptionHandler(value = {DuplicateEntryException.class, InvalidRequestException.class})
    @RequestMapping(produces = "application/vnd.error+json")
    public ResponseEntity<RequestResponse> duplicateEntryExceptionHandler(DefaultException exception){

        System.out.println("welcome for advice");
        exception.printStackTrace();

        RequestResponse response = new RequestResponse();
        response.setStatusToFailed();
        response.setMessage(exception.getMessage());
        HttpHeaders headers = new HttpHeaders();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(response, headers,status);
    }





}
