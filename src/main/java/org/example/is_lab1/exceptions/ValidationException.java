package org.example.is_lab1.exceptions;

/**
 * Исключение, выбрасываемое при ошибках валидации данных.
 * Используется при валидации объектов перед сохранением в БД.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}












