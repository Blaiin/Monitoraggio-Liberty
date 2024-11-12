package it.dmi.structure.exceptions.impl.persistence;

import it.dmi.structure.exceptions.MSDException;

public class InvalidCredentialsException extends MSDException {
    public InvalidCredentialsException (String message) {
        super(message);
    }
    @SuppressWarnings("unused")
    public InvalidCredentialsException (Throwable e) {
        super(e);
    }
}
