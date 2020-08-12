package com.bloyot.recordhomeworkcommon;

/**
 * Exception for indicating there was an issue parsing a file for a record.
 */
public class RecordParseException extends Exception {

    public RecordParseException(String message) {
        super(message);
    }

    public RecordParseException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
