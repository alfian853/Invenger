package com.bliblifuture.invenger.exception;

import lombok.Data;

@Data
public class InvalidRequestException extends DefaultRuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }

    public InvalidRequestException() {

    }
}
