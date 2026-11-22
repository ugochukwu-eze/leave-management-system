package org.example.crown.leavemanagementassessment.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum BusinessErrorCodes {
    MISSING_SERVLET_REQUEST(30, HttpStatus.BAD_REQUEST, "Missing required parameter."),
    INVALID_DATE_REQUEST_EXCEPTION(31, HttpStatus.CONFLICT, "Start date cannot be after and date"),
    PARAM_NOT_FOUND(32, HttpStatus.NOT_FOUND, ""),
    UNAUTHORIZED(33, HttpStatus.FORBIDDEN, "ONLY ADMIN CAN ASSIGN ROLES."),
    NOT_FOUND(34, HttpStatus.NOT_FOUND , "Employee not found."),
    USER_ALREADY_EXIST(35, HttpStatus.CONFLICT , "THE USER ALREADY EXIST."),
    USER_ALREADY_CONFIGURED(44, HttpStatus.CONFLICT , "THIS ACCOUNT HAS ALREADY BEEN CONFIGURED."),
    TRANSACTION_NOT_FOUND(36, HttpStatus.CONFLICT , "LEAVE RECORD IS NOT FOUND."),
    INSUFFICIENT_LEAVE(37, HttpStatus.NOT_FOUND , ""),
    DATE_IN_THE_PAST(38, HttpStatus.CONFLICT , ""),
    FREQUENCY_TYPE(39, HttpStatus.BAD_REQUEST , "At-least One period must be chosen."),
    INTERNAL_SERVER_ERROR(40, HttpStatus.INTERNAL_SERVER_ERROR , "FAILED TO SEND MAIL."),
    TOKEN_NOT_FOUND(41, HttpStatus.NOT_FOUND , "TOKEN IS NOT FOUND."),
    EXPIRED_TOKEN(42, HttpStatus.UNAUTHORIZED , "ACTIVATION TOKEN HAS EXPIRED."),
    TRANSACTION_NOT_APPROVED(43, HttpStatus.UNAUTHORIZED , "TRANSACTION IS NOT APPROVED."),
    INVALID_REQUEST_EXCEPTION(44, HttpStatus.BAD_REQUEST , "Only pending leave requests can be approved or rejected."),
    TERMINATION_REQUEST_EXCEPTION(45, HttpStatus.CONFLICT , "You already have an overlapping leave request.");



    @Getter
    private final int code;

    @Getter
    private final String description;

    @Getter
    private final HttpStatus status;


    BusinessErrorCodes(int code, HttpStatus status, String description) {
        this.code = code;
        this.description = description;
        this.status = status;
    }
}
