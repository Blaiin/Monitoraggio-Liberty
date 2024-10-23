package it.dmi.structure.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MSDRuntimeException extends RuntimeException {

    public MSDRuntimeException (String message) {
        super(message);
    }

    public MSDRuntimeException (Throwable cause) {
        super(cause);
    }

    public MSDRuntimeException (String msg, Throwable cause) {
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
