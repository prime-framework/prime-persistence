package org.primeframework.util;

import org.primeframework.persistence.util.ExceptionTools;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author James Humphrey
 */
@Test(groups = "unit")
public class ExceptionToolsTest {

  @Test
  public void findCause() {

    Throwable throwable = new Exception(new MockCauseOne());
    Throwable t = ExceptionTools.findCause(throwable);
    assertTrue(t instanceof MockCauseOne);

    throwable = new Exception(new MockCauseOne(new MockCauseTwo(new MockCauseThree())));
    t = ExceptionTools.findCause(throwable);
    assertTrue(t instanceof MockCauseThree);
  }

  public static class MockCauseOne extends Throwable {

    public MockCauseOne() {
    }

    public MockCauseOne(Throwable cause) {
      super(cause);
    }
  }

  public static class MockCauseTwo extends Throwable {

    public MockCauseTwo() {
    }

    public MockCauseTwo(Throwable cause) {
      super(cause);
    }
  }

  public static class MockCauseThree extends Throwable {

    public MockCauseThree() {
    }

    public MockCauseThree(Throwable cause) {
      super(cause);
    }
  }
}