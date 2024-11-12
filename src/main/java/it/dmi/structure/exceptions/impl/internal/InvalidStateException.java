package it.dmi.structure.exceptions.impl.internal;

import it.dmi.structure.exceptions.MSDRuntimeException;

@SuppressWarnings("unused")
public class InvalidStateException extends MSDRuntimeException {

    public InvalidStateException() {
    }

    public InvalidStateException(String message) {
        super(message);
    }

    public InvalidStateException(Throwable cause) {
        super(cause);
    }

    public InvalidStateException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
