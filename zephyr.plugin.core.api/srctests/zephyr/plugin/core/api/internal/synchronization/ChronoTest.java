package zephyr.plugin.core.api.internal.synchronization;

import junit.framework.Assert;

import org.junit.Test;

import zephyr.plugin.core.api.synchronization.Chrono;

public class ChronoTest {
  @Test
  public void testChrono() throws InterruptedException {
    Chrono chrono = new Chrono();
    Thread.sleep(31);
    long millis = chrono.getCurrentMillis();
    double sec = chrono.getCurrentChrono();
    Assert.assertTrue(millis >= 30);
    Assert.assertTrue(millis < 1000);
    Assert.assertTrue(sec >= 0.03);
    Assert.assertTrue(sec < 1.0);
    chrono.toString();
  }
}
