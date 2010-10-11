package zephyr.plugin.core.api.logging.fileloggers;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;

import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.monitoring.fileloggers.FileLogger;
import zephyr.plugin.core.api.monitoring.wrappers.Abs;
import zephyr.plugin.core.api.monitoring.wrappers.Squared;


public class ParserWrappersTest {
  static protected final String[] expectedLabels = new String[] { "data01", "data01Squared", "data01Abs" };

  @Monitor(wrappers = { Squared.ID, Abs.ID })
  protected double data01 = -2;

  @Test
  public void testAddLog() {
    StringWriter writer = new StringWriter();
    FileLogger logger = new FileLogger(writer);
    logger.add(this);
    Assert.assertArrayEquals(expectedLabels, logger.getLabels());
    logger.update(0);
    Assert.assertEquals("data01 data01Squared data01Abs\n-2.0 4.0 2.0\n", writer.toString());
  }
}
