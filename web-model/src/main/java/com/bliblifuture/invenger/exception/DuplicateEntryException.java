package com.bliblifuture.invenger.exception;

import lombok.Data;

@Data
public class DuplicateEntryException extends DefaultException {

    public DuplicateEntryException(String message){
        super(message);
    }

}
