package ru.ardyc.refwrapper.exception;

public class UnknownBehaviorException extends IllegalArgumentException {
    public UnknownBehaviorException(String methodName) {
        super("Unknown behavior of the method " + methodName + ". You may have forgotten the @Method or @Field annotation");
    }
}
