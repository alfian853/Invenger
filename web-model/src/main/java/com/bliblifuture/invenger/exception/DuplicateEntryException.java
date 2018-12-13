package com.bliblifuture.invenger.exception;

import lombok.Data;

@Data
public class DuplicateEntryException extends DefaultRuntimeException {

    public DuplicateEntryException(String message){
        super(message);
    }

}
