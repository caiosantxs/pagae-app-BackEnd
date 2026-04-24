package com.example.pagae_app.exception;


public class ErrorResponse {

    private String message;
    private Integer status;

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public ErrorResponse(String message, Integer status) {
        this.message = message;
        this.status = status;
    }

    public ErrorResponse() {}
}
