package zephyr.plugin.core.api.codeparser.parsers;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;

import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.monitoring.fileloggers.FileLogger;


public class ParserBooleanTest {
  static final String[] expectedLabels = new String[] { "data01" };
  @Monitor
  protected boolean data01;

  @Test
  public void testAddLog() {
    StringWriter writer = new StringWriter();
    FileLogger logger = new FileLogger(writer);
    logger.add(this);
    logger.printLegend();
    Assert.assertArrayEquals(expectedLabels, logger.getLabels());
    data01 = false;
    logger.update(0);
    data01 = true;
    logger.update(1);
    data01 = false;
    logger.update(2);
    Assert.assertEquals("data01\n0.0\n1.0\n0.0\n", writer.toString());
  }
}
