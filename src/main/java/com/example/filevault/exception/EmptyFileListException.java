package com.example.filevault.exception;

public class EmptyFileListException extends RuntimeException{
    public EmptyFileListException(String message) {
        super(message);
    }
}
