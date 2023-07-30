package ru.ardyc.refwrapper.exception;

public class UnknownFieldException extends IllegalArgumentException {
    public UnknownFieldException(String fieldName) {
        super("Field " + fieldName + " not found in target class");
    }
}
