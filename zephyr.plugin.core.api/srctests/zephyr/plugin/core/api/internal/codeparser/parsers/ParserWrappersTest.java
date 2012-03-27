package zephyr.plugin.core.api.internal.codeparser.parsers;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;

import zephyr.plugin.core.api.internal.monitoring.fileloggers.FileLogger;
import zephyr.plugin.core.api.internal.monitoring.wrappers.Abs;
import zephyr.plugin.core.api.internal.monitoring.wrappers.Squared;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;


public class ParserWrappersTest {
  static final String[] expectedLabels = new String[] { "data01", "data01Squared", "data01Abs" };

  @Monitor(wrappers = { Squared.ID, Abs.ID })
  protected double data01 = -2;

  @Test
  public void testAddLog() {
    StringWriter writer = new StringWriter();
    FileLogger logger = new FileLogger(writer);
    logger.add(this);
    logger.printLegend();
    Assert.assertArrayEquals(expectedLabels, logger.getLabels());
    logger.update(0);
    Assert.assertEquals("data01 data01Squared data01Abs\n-2.0 4.0 2.0\n", writer.toString());
  }
}
