package zephyr.plugin.core.api.logging.fileloggers;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import zephyr.plugin.core.api.monitoring.fileloggers.LoggerRow;

public class LoggerRowTest {
  private StringWriter writer = null;
  private LoggerRow logger = null;

  @Before
  public void setUp() {
    writer = new StringWriter();
    logger = new LoggerRow(writer);
  }

  @Test
  public void testLogger() {
    logger.writeLegend("n", "a");
    logger.writeRow(1.5, 4.5);
    logger.writeRow(1.2, 46.4);
    Assert.assertEquals("n a\n1.5 4.5\n1.2 46.4\n", writer.toString());
  }
}
