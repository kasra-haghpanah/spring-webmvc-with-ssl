package org.application.spring.configuration.exception;

public class ApplicationException extends RuntimeException {

    private int statusCode;
    private Object[] variables;

    public ApplicationException(String message, int statusCode, Object... variables) {
        super(message);
        this.statusCode = statusCode;
        this.variables = variables;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Object[] getVariables() {
        return variables;
    }

    public void setVariables(Object[] variables) {
        this.variables = variables;
    }
}
