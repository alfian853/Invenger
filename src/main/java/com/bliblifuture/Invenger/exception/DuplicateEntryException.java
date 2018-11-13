package com.bliblifuture.Invenger.exception;

import lombok.Data;

@Data
public class DuplicateEntryException extends DefaultException {

    public DuplicateEntryException(String message){
        super(message);
    }

}
