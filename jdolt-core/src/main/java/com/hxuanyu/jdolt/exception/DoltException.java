package com.hxuanyu.jdolt.exception;

/**
 * Custom exception class for signaling errors related to Dolt operations.
 * This exception serves as a base class for more specific exceptions within the Dolt ecosystem,
 * encapsulating error messages and underlying causes, enhancing error handling and traceability.
 */
public class DoltException extends RuntimeException{
    public DoltException(String message) {
        super(message);
    }

    public DoltException(String message, Throwable cause) {
        super(message, cause);
    }
}
