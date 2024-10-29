package it.dmi.structure.exceptions.impl.internal;

import it.dmi.structure.exceptions.MSDRuntimeException;

@SuppressWarnings("unused")
public class DependencyInjectionException extends MSDRuntimeException {

    private DependencyInjectionException() {}

    public DependencyInjectionException(String message) {
        super(message);
    }

    public DependencyInjectionException(Throwable cause) {
        super(cause);
    }

    public DependencyInjectionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
