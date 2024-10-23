package it.dmi.structure.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MSDException extends Exception {

    public MSDException (String message) {
        super(message);
    }

    public MSDException (Throwable cause) {
        super(cause);
    }

    public MSDException (String msg, Throwable cause) {
        super(msg, cause);
    }

    public Throwable getUnderlyingException() {
        return super.getCause();
    }

    public String toString() {
        Throwable cause = this.getUnderlyingException();
        if (cause != null && cause != this) {
            var sup = super.toString();
            return sup + " [See nested exception: " + cause + "]";
        } else {
            return super.toString();
        }
    }
}
