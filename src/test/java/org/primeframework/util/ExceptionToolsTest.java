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
  public void getOriginalCause() {

    Throwable throwable = new Exception(new MockCauseOne());
    Throwable t = ExceptionTools.getOriginalCause(throwable);
    assertTrue(t instanceof MockCauseOne);

    throwable = new Exception(new MockCauseOne(new MockCauseTwo(new MockCauseThree())));
    t = ExceptionTools.getOriginalCause(throwable);
    assertTrue(t instanceof MockCauseThree);
  }

  @Test
  public void findCause() {
    Throwable throwable = new Exception();
    assertFalse(ExceptionTools.containsCause(throwable, MockCauseOne.class));

    throwable = new Exception(new MockCauseOne(new MockCauseTwo(new MockCauseThree())));
    assertTrue(ExceptionTools.containsCause(throwable, MockCauseThree.class));
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