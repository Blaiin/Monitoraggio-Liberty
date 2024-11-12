package it.dmi.structure.exceptions.impl.persistence;

import it.dmi.structure.exceptions.MSDException;

public class DatabaseConnectionException extends MSDException {
    @SuppressWarnings("unused")
    public DatabaseConnectionException (String message) {
        super(message);
    }
    public DatabaseConnectionException(Throwable e) {
        super(e);
    }
}
