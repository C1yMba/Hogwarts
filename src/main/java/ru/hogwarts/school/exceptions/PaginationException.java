package ru.hogwarts.school.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PaginationException extends RuntimeException {

    private final HttpStatus status;

    public PaginationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
