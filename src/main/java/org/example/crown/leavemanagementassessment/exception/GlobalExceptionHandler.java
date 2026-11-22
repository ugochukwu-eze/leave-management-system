package org.example.crown.leavemanagementassessment.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);



    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleException(AccessDeniedException ex){

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.UNAUTHORIZED.getCode())
//                        .businessErrorDescription(BusinessErrorCodes.UNAUTHORIZED.getDescription())
                        .error(ex.getMessage())
                        .build());

    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ExceptionResponse> handleException(MissingServletRequestParameterException ex){

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.MISSING_SERVLET_REQUEST.getCode())
//                        .businessErrorDescription(BusinessErrorCodes.MISSING_SERVLET_REQUEST.getDescription())
                        .error(ex.getMessage())
                        .build());

    }

    @ExceptionHandler(ParamNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(ParamNotFoundException ex){

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.PARAM_NOT_FOUND.getCode())
//                        .businessErrorDescription(BusinessErrorCodes.MISSING_SERVLET_REQUEST.getDescription())
                        .error(ex.getMessage())
                        .build());

    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException ex){

        Set<String> errors = new HashSet<>();

        ex.getBindingResult().getAllErrors()
                .forEach(error -> {
                  var errorMessage =  error.getDefaultMessage();
                  errors.add(errorMessage);
                });
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .validationErrors(errors)
                        .build());

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception ex){
        logger.error("::::::Exception occurred at handle Exception Method::::{}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.builder()
                        .businessErrorDescription("Internal Error, contact the admin")
                        .build());

    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleException(UserAlreadyExistsException ex){

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.USER_ALREADY_EXIST.getCode())
                        .businessErrorDescription(BusinessErrorCodes.USER_ALREADY_EXIST.getDescription())
                        .error(ex.getMessage())
                        .build());

    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(UserNotFoundException ex){

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.NOT_FOUND.getCode())
//                        .businessErrorDescription(BusinessErrorCodes.NOT_FOUND.getDescription())
                        .error(ex.getMessage())
                        .build());

    }

    @ExceptionHandler(DateInThePastException.class)
    public ResponseEntity<ExceptionResponse> handleException(DateInThePastException ex){

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.DATE_IN_THE_PAST.getCode())
//                        .businessErrorDescription(BusinessErrorCodes.PASSWORD_MISMATCH.getDescription())
                        .error(ex.getMessage())
                        .build());

    }

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(RecordNotFoundException ex){

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.TRANSACTION_NOT_FOUND.getCode())
                        .businessErrorDescription(BusinessErrorCodes.TRANSACTION_NOT_FOUND.getDescription())
                        .error(ex.getMessage())
                        .build());

    }

    @ExceptionHandler(FrequencyTypeException.class)
    public ResponseEntity<ExceptionResponse> handleException(FrequencyTypeException ex){

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.FREQUENCY_TYPE.getCode())
                        .businessErrorDescription(BusinessErrorCodes.FREQUENCY_TYPE.getDescription())
                        .error(ex.getMessage())
                        .build());

    }

    @ExceptionHandler(InsufficientLeaveException.class)
    public ResponseEntity<ExceptionResponse> handleException(InsufficientLeaveException ex){

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.INSUFFICIENT_LEAVE.getCode())
//                        .businessErrorDescription(BusinessErrorCodes.USER_CANNOT_PRINT.getDescription())
                        .error(ex.getMessage())
                        .build());

    }

    @ExceptionHandler(InvalidDateRequestException.class)
    public ResponseEntity<ExceptionResponse> handleException(InvalidDateRequestException ex){

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.INVALID_DATE_REQUEST_EXCEPTION.getCode())
//                        .businessErrorDescription(BusinessErrorCodes.USER_CANNOT_PRINT.getDescription())
                        .error(ex.getMessage())
                        .build());

    }

    @ExceptionHandler(AccountAlreadyConfigured.class)
    public ResponseEntity<ExceptionResponse> handleException(AccountAlreadyConfigured ex){

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.USER_ALREADY_CONFIGURED.getCode())
                        .businessErrorDescription(BusinessErrorCodes.USER_ALREADY_CONFIGURED.getDescription())
                        .error(ex.getMessage())
                        .build());

    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ExceptionResponse> handleException(InvalidRequestException ex){

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.INVALID_REQUEST_EXCEPTION.getCode())
//                        .businessErrorDescription(BusinessErrorCodes.INVALID_REQUEST_EXCEPTION.getDescription())
                        .error(ex.getMessage())
                        .build());

    }

    @ExceptionHandler(OverlappingException.class)
    public ResponseEntity<ExceptionResponse> handleException(OverlappingException ex){

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.TERMINATION_REQUEST_EXCEPTION.getCode())
//                        .businessErrorDescription(BusinessErrorCodes.TERMINATION_REQUEST_EXCEPTION.getDescription())
                        .error(ex.getMessage())
                        .build());

    }


}
