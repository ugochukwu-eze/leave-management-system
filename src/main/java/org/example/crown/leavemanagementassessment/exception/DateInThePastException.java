package org.example.crown.leavemanagementassessment.exception;

public class DateInThePastException extends RuntimeException {
    public DateInThePastException(String message) {
        super(message);
    }
}
