package it.dmi.structure.exceptions.impl.quartz;

import it.dmi.structure.exceptions.MSDRuntimeException;

@SuppressWarnings("unused")
public class JobAlreadyDefinedException extends MSDRuntimeException {

    public JobAlreadyDefinedException(String message) {
        super(message);
    }

    public JobAlreadyDefinedException(Throwable cause) {
        super(cause);
    }

    public JobAlreadyDefinedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public JobAlreadyDefinedException() {
    }
}
