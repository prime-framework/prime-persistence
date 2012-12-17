package org.primeframework.persistence.util;

/**
 * @author James Humphrey
 */
public class ExceptionTools {

  /**
   * Returns the leaf node level cause
   *
   * @param t the original exception
   * @return the leaf node level cause
   */
  public static Throwable getOriginalCause(Throwable t) {
    Throwable cause = t.getCause();

    if (cause.getCause() != null) {
      return getOriginalCause(cause);
    }

    return cause;
  }

  /**
   * Returns true if this throwable contains any throwable that is assignable from that throwable
   *
   * @param thisT the throwable to perform the contains on
   * @param thatT the throwable to match is assignable from
   * @return true or false
   */
  public static boolean containsCause(Throwable thisT, Class<? extends Throwable> thatT) {
    return thisT.getClass().isAssignableFrom(thatT) || thisT.getCause() != null && containsCause(thisT.getCause(), thatT);
  }
}
