package com.proj.exception;

public class InvalidStageTransitionException extends RuntimeException {

    public InvalidStageTransitionException(String message) {
        super(message);
    }
}
