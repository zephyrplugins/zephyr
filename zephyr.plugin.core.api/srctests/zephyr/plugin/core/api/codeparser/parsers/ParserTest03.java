package zephyr.plugin.core.api.codeparser.parsers;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;

import zephyr.plugin.core.api.monitoring.annotations.IgnoreMonitor;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.monitoring.fileloggers.FileLogger;


public class ParserTest03 {
  static final String[] expectedLabels = new String[] { "data01", "ignoreLabel" };

  @Monitor
  static public class TestObjectMonitored {
    protected double data01;
    @IgnoreMonitor
    protected double data02;
    protected TestIgnoreLabel ignoreLabel = new TestIgnoreLabel();
  }

  static public class TestIgnoreLabel {
    @Monitor(skipLabel = true)
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
