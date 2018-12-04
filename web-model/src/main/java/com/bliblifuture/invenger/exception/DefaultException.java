package com.bliblifuture.invenger.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefaultException extends Exception {

    protected String message;

}
