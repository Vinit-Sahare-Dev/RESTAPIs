package com.spring.restapi.exception;

import java.time.LocalDateTime;

public class APIErrorResponse {
    private int statusCode;
    private String message;
    private LocalDateTime dateTime;

    public void setStatusCode(int code) { this.statusCode = code; }
    public void setMessage(String msg) { this.message = msg; }
    public void setDateTime(LocalDateTime dt) { this.dateTime = dt; }

    public int getStatusCode() { return statusCode; }
    public String getMessage() { return message; }
    public LocalDateTime getDateTime() { return dateTime; }
}
