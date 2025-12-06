package kr.ac.jbnu.cr.bookstore.exception;

public class StateConflictException extends RuntimeException {

    public StateConflictException(String message) {
        super(message);
    }
}