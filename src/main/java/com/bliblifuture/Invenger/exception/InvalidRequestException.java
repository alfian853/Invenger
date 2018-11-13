package com.bliblifuture.Invenger.exception;

import lombok.Data;

@Data
public class InvalidRequestException extends DefaultException {

    public InvalidRequestException(String message) {
        super(message);
    }

}
