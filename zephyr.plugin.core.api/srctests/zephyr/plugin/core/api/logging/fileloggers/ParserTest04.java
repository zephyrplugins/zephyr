package zephyr.plugin.core.api.logging.fileloggers;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;

import zephyr.plugin.core.api.logging.fileloggers.FileLogger;
import zephyr.plugin.core.api.monitoring.DataLogged;


public class ParserTest04 {
  static protected final String[] expectedLabels = new String[] { "data01" };

  @DataLogged
  static public class TestAddLog01 {
    protected boolean data01;
  }

  @Test
  public void testAddLog() {
    StringWriter writer = new StringWriter();
    FileLogger logger = new FileLogger(writer);
    TestAddLog01 logged01 = new TestAddLog01();
    logger.add(logged01);
    Assert.assertArrayEquals(expectedLabels, logger.getLabels());
    logged01.data01 = false;
    logger.update(0);
    logged01.data01 = true;
    logger.update(1);
    logged01.data01 = false;
    logger.update(2);
    Assert.assertEquals("data01\n0.0\n1.0\n0.0\n", writer.toString());
  }
}
