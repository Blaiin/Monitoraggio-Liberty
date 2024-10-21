package it.dmi.utils.exceptions;

public class QueryFailureException extends Exception {

    public QueryFailureException(String message) {
        super(message);
    }

    public QueryFailureException(String message, String faultyQuery) {
        super("Faulty query: " + faultyQuery + ". " + message);
    }

    public QueryFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
