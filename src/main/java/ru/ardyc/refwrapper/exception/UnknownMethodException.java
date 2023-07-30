package ru.ardyc.refwrapper.exception;

public class UnknownMethodException extends IllegalArgumentException {
    public UnknownMethodException(String methodName) {
        super("Method " + methodName + " not found in target class");
    }
}
