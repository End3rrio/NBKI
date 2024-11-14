package org.example.exception;

public class RecordNotFoundException extends RuntimeException {
    private final static String RECORD_NOT_FOUND_BY_ID = "Record not found by ID";

    public RecordNotFoundException() {
        super(String.format(RECORD_NOT_FOUND_BY_ID));
    }
}
