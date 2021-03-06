package zephyr.plugin.core.api.internal.logging.fileloggers;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import zephyr.plugin.core.api.internal.monitoring.fileloggers.FileLogger;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;

public class TimeStampLoggerTest {
  protected double a = 0.0;
  protected Monitored logA = new Monitored() {
    @Override
    public double monitoredValue() {
      return a;
    }
  };
  protected double b = 0.0;
  protected Monitored logB = new Monitored() {
    @Override
    public double monitoredValue() {
      return b;
    }
  };
  private StringWriter writer = null;
  private FileLogger logger = null;

  @Before
  public void setUp() {
    writer = new StringWriter();
    logger = new FileLogger(writer, true);
    logger.add("a", logA);
    logger.add("b", logB);
    logger.printLegend();
  }

  private void testOutput(int stepTime, double a, double b, boolean legendIncluded) {
    final String legend = "LocalTime a b";
    this.a = a;
    this.b = b;
    long beforeTime = System.currentTimeMillis();
    logger.update(stepTime);
    String lineLogged;
    if (legendIncluded) {
      String[] lines = writer.toString().split("\n");
      Assert.assertEquals(legend, lines[0]);
      lineLogged = lines[1];
    } else
      lineLogged = writer.toString();
    String[] values = lineLogged.split(" ");
    Assert.assertEquals(3, values.length);
    Assert.assertEquals(a, Double.parseDouble(values[1]), 0.0);
    Assert.assertEquals(b, Double.parseDouble(values[2]), 0.0);
    long afterTime = System.currentTimeMillis();
    long loggedTime = Long.parseLong(values[0]);
    Assert.assertTrue(loggedTime >= beforeTime && loggedTime <= afterTime);
    writer.getBuffer().delete(0, writer.toString().length());
  }

  @Test
  public void testLogger() {
    testOutput(0, 5.0, 6.0, true);
    testOutput(1, 4.0, 3.0, false);
  }
}
