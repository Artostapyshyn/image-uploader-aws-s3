package com.artostapyshyn.image.manager.exceptions.handler;

import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(500).body("Internal Server Error: " + ex.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Object> handleIOException(IOException ex) {
        return ResponseEntity.status(500).body("IO Error: " + ex.getMessage());
    }

    @ExceptionHandler(AmazonRekognitionException.class)
    public ResponseEntity<Object> handleAmazonRekognitionException(AmazonRekognitionException ex) {
        return ResponseEntity.status(500).body("Amazon Rekognition Error: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
        return ResponseEntity.status(500).body("An unexpected error occurred: " + ex.getMessage());
    }
}
