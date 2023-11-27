package com.example.exception;

public class ResourceNotFoundException extends RuntimeException{
    /* Added this custom exception class for show, but it's not been used
     * in this project. 
     * 
    */
    public ResourceNotFoundException(String message) {
		super(message);
	}
}