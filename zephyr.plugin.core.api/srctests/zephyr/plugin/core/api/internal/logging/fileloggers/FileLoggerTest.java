package zephyr.plugin.core.api.internal.logging.fileloggers;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;

import zephyr.plugin.core.api.internal.monitoring.fileloggers.AbstractFileLogger;
import zephyr.plugin.core.api.internal.monitoring.fileloggers.FileLogger;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;

public class FileLoggerTest {
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
    logger = new FileLogger(writer);
    logger.add("a", logA);
    logger.add("b", logB);
  }

  private void testOutput(int stepTime, double a, double b, String expected) {
    testOutput(stepTime, a, b, expected, false);
  }

  public void testOutput(int stepTime, double a, double b, String expected, boolean legendIncluded) {
    final String legend = "a b\n";
    this.a = a;
    this.b = b;
    if (stepTime == 0 && legendIncluded)
      logger.printLegend();
    logger.update(stepTime);
    String expectedLogged = expected;
    if (legendIncluded)
      expectedLogged = legend + expectedLogged;
    Assert.assertEquals(expectedLogged, writer.toString());
    writer.getBuffer().delete(0, writer.toString().length());
  }

  @Test
  public void testLogger() {
    testOutput(0, 5.0, 6.0, "5.0 6.0\n", true);
    testOutput(1, 4.0, 3.0, "4.0 3.0\n");
  }

  @Test
  public void testTemporaryFile() throws IOException {
    File file = File.createTempFile("junit", ".test");
    Assert.assertTrue(file.canRead());
    if (!file.delete())
      throw new RuntimeException("Could not delete " + file.getAbsolutePath());
    Assert.assertFalse(file.canRead());
    FileLogger logger = new FileLogger(file.getAbsolutePath());
    Assert.assertTrue(file.canRead());
    logger.close();
    if (!file.delete())
      throw new RuntimeException("Could not delete " + file.getAbsolutePath());
    logger = new FileLogger(file.getAbsolutePath(), false, true);
    Assert.assertTrue(new File(file.getAbsolutePath() + AbstractFileLogger.TEMP).canRead());
    Assert.assertFalse(file.canRead());
    logger.close();
    Assert.assertTrue(file.canRead());
    if (!file.delete())
      throw new RuntimeException("Could not delete " + file.getAbsolutePath());
  }
}
