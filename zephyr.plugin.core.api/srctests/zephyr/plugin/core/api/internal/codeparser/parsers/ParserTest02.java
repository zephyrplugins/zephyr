package zephyr.plugin.core.api.internal.codeparser.parsers;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import zephyr.plugin.core.api.internal.monitoring.fileloggers.FileLogger;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;


public class ParserTest02 {
  static final String[] expectedLabels = new String[] { "doubleCollection[0]", "doubleCollection[1]" };

  @Monitor
  static class TestListDoubleMonitored {
    private final List<Double> doubleCollection = new ArrayList<Double>();

    public TestListDoubleMonitored() {
      doubleCollection.add(1.0);
      doubleCollection.add(2.0);
    }
  }

  @Test
  public void testAddLog() {
    FileLogger logger = new FileLogger(new StringWriter());
    TestListDoubleMonitored logged01 = new TestListDoubleMonitored();
    logger.add(logged01);
    Assert.assertArrayEquals(expectedLabels, logger.getLabels());
  }
}
