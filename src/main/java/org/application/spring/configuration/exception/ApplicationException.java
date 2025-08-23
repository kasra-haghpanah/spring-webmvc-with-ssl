package org.application.spring.configuration.exception;

import org.springframework.http.HttpStatus;

public class ApplicationException extends RuntimeException {

    private int statusCode;
    private Object[] variables;

    public ApplicationException(String message, HttpStatus statusCode, Object... variables) {
        super(message);
        this.statusCode = statusCode.value();
        this.variables = variables;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setStatusCode(HttpStatus statusCode) {
        this.statusCode = statusCode.value();
    }

    public Object[] getVariables() {
        return variables;
    }

    public void setVariables(Object[] variables) {
        this.variables = variables;
    }
}
