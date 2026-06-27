package tn.iteam.meduserservice.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tn.iteam.meduserservice.dtos.responses.ErrorResponseDto;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(ResourceNotFoundException exception,
                                                                            HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateResourceException(DuplicateResourceException exception,
                                                                             HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponseDto> handleBusinessRuleException(BusinessRuleException exception,
                                                                        HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler({AuthorizationDeniedException.class, org.springframework.security.access.AccessDeniedException.class})
    public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(Exception exception,
                                                                        HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "Access denied.", request.getRequestURI());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleBadCredentialsException(BadCredentialsException exception,
                                                                          HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid email or password.", request.getRequestURI());
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponseDto> handleDisabledException(DisabledException exception,
                                                                    HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "User account is inactive.", request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception,
                                                                                  HttpServletRequest request) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return buildResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException exception,
                                                                           HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI());
    }

    private ResponseEntity<ErrorResponseDto> buildResponse(HttpStatus status, String message, String path) {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
        return ResponseEntity.status(status).body(errorResponseDto);
    }
}
