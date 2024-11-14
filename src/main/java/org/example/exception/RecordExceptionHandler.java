package org.example.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
public class RecordExceptionHandler {

    @ExceptionHandler(value = {RecordNotFoundException.class})
    public ResponseEntity<ErrorResponseDto> handleNotFound(Exception exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(new ErrorResponseDto(exception.getMessage()), HttpStatus.NOT_FOUND);
    }
}
