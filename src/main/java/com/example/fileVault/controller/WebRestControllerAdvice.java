package com.example.fileVault.controller;

import com.example.fileVault.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WebRestControllerAdvice {
    @ExceptionHandler({FileNotFoundException.class,
                      CloseStreamException.class, PutNextEntryToZipException.class,
                      ReadWriteStreamException.class})
    public ResponseEntity<String> handleNotFoundException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler({BadFileTypeException.class, EmptyFileNameException.class, CantReadFileContentException.class})
    public ResponseEntity<String> handleFileNotSaved(Exception e) {
        return ResponseEntity.badRequest().body("File is not saved! Error: " + e.getMessage());
    }

    @ExceptionHandler(EmptyFileListException.class)
    public ResponseEntity<String> handleEmptyFileListException(Exception e) {
        return ResponseEntity.badRequest().body("Error reading all files: " + e.getMessage());
    }

    @ExceptionHandler(TooLargeFileSizeException.class)
    public ResponseEntity<String> handleTooLargeFileSizeException(Exception e) {
        return  ResponseEntity.badRequest().body(e.getMessage());
    }
}
