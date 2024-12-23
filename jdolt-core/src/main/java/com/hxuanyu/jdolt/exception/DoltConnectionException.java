package com.hxuanyu.jdolt.exception;

/**
 * Exception class representing errors that occur during attempts to establish or manage a connection to a Dolt database.
 * This exception is typically thrown when there is an issue with the connection parameters, network problems,
 * or when the database server is unreachable.
 */
public class DoltConnectionException extends DoltException {
    public DoltConnectionException(String message) {
        super(message);
    }

    public DoltConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}