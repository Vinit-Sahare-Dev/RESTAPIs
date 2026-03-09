package com.spring.restapi.exception;


public class IllegalDepartmentException extends RuntimeException {
    public IllegalDepartmentException(String message) {
        super(message);
    }
}

