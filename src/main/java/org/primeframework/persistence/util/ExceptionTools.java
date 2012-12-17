package org.primeframework.persistence.util;

/**
 * @author James Humphrey
 */
public class ExceptionTools {

  public static Throwable findCause(Throwable t) {
    Throwable cause = t.getCause();

    if (cause.getCause() != null) {
      return findCause(cause);
    }

    return cause;
  }
}
