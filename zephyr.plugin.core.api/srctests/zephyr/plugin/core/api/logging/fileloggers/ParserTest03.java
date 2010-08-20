package zephyr.plugin.core.api.logging.fileloggers;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;

import zephyr.plugin.core.api.monitoring.DataIgnored;
import zephyr.plugin.core.api.monitoring.DataLogged;


public class ParserTest03 {
  static protected final String[] expectedLabels = new String[] { "data01", "ignoreLabel" };

  @DataLogged
  static public class TestObjectMonitored {
    protected double data01;
    @DataIgnored
    protected double data02;
    protected TestIgnoreLabel ignoreLabel = new TestIgnoreLabel();
  }

  static public class TestIgnoreLabel {
    @DataLogged(skipLabel = true)
    protected double myFieldNameIsIgnored;
  }

  @Test
  public void testAddLog() {
    FileLogger logger = new FileLogger(new StringWriter());
    TestObjectMonitored logged01 = new TestObjectMonitored();
    logger.add(logged01);
    Assert.assertArrayEquals(expectedLabels, logger.getLabels());
  }
}
