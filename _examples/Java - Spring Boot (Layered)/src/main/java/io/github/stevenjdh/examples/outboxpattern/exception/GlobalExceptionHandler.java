/*
 * This file is part of Outbox Pattern <https://github.com/StevenJDH/outbox-pattern>.
 * Copyright (c) 2025 Steven Jenkins De Haro
 * 
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/MIT.
 */

package io.github.stevenjdh.examples.outboxpattern.exception;

import io.github.stevenjdh.examples.outboxpattern.dto.response.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class.getName());

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex,
                                                                       HttpServletRequest request) {

        Map<String, String> validationErrors = new HashMap<>();
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            validationErrors.put(field, message);
        });

        String message = "Request had validation errors";
        LOG.error("HTTP Code [{}] - {}: [{}]", status.value(), message, validationErrors);

        return buildErrorResponse(status, message + ".", request, validationErrors);
    }
    
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoHandlerFoundException(NoHandlerFoundException ex,
                                                                          HttpServletRequest request) {
        
        HttpStatus status = HttpStatus.NOT_FOUND;
        String message = "The requested resource was not found";
        LOG.error("HTTP Code [{}] - {}: [{} {}]", status.value(), message,
                request.getMethod(), request.getRequestURI());

        return buildErrorResponse(status, message + ".", request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex,
                                                                           HttpServletRequest request) {

        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        LOG.error("HTTP Code [{}] - {}", status.value(), ex.getMessage());
        return buildErrorResponse(status, ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneralException(Exception ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        LOG.error("HTTP Code [{}] - {}", status.value(), ex.getMessage());
        return buildErrorResponse(status, ex.getMessage(), request);
    }

    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(HttpStatus status,
                                                                String message,
                                                                HttpServletRequest request) {
        
        return buildErrorResponse(status, message, request, null);
    }

    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(HttpStatus status,
                                                                String message,
                                                                HttpServletRequest request,
                                                                Map<String, String> validationErrors) {

        var errorResponse = new ErrorResponseDTO(
                OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                status.value(),
                status.getReasonPhrase(),
                request.getRequestURI(),
                message,
                validationErrors
        );

        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(errorResponse);
    }
}
