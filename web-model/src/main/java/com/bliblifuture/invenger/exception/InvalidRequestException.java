package com.bliblifuture.invenger.exception;

import lombok.Data;

@Data
public class InvalidRequestException extends DefaultException {

    public InvalidRequestException(String message) {
        super(message);
    }

}
