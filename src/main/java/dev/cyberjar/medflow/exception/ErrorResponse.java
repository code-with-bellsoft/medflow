package dev.cyberjar.medflow.exception;

public class ErrorResponse {

    String exception;
    int code;

    public ErrorResponse(String exception) {
        this.exception = exception;
    }

    public ErrorResponse(int code, String exception){
        this.code = code;
        this.exception = exception;
    }

    public String getException() {
        return exception;
    }
}
