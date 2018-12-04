package com.bliblifuture.invenger.exception;

import lombok.Data;

@Data
public class DataNotFoundException extends DefaultException {

    public DataNotFoundException(String message){
        super(message);
    }
}
