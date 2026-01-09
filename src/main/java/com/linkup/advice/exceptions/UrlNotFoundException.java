package com.linkup.advice.exceptions;

public class UrlNotFoundException extends RuntimeException {
    public UrlNotFoundException(String message){
        super(message);
    }
}
