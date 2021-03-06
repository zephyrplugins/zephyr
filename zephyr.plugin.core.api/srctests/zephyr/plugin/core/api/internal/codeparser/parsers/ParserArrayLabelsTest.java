package zephyr.plugin.core.api.internal.codeparser.parsers;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;

import zephyr.plugin.core.api.internal.monitoring.fileloggers.FileLogger;
import zephyr.plugin.core.api.monitoring.annotations.LabelProvider;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;


public class ParserArrayLabelsTest {
  static final String[] expectedLabels = new String[] { "logged01", "logged01", "logged02a",
      "logged02b", "logged03/data01" };

  static public class TestObjectMonitored {
    @Monitor
    protected double data01;
  }

  @Monitor(arrayDecoration = false)
  protected final double[] logged01 = new double[2];
  @Monitor(arrayDecoration = false)
  protected final double[] logged02 = new double[2];
  @Monitor(arrayDecoration = false)
  protected final TestObjectMonitored[] logged03 = { new TestObjectMonitored() };

  @LabelProvider(ids = { "logged02" })
  protected String labelOf(int index) {
    return Character.toString((char) ('a' + index));
  }

  @Test
  public void testAddLog() {
    FileLogger logger = new FileLogger(new StringWriter());
    logger.add(this);
    Assert.assertArrayEquals(expectedLabels, logger.getLabels());
  }
}
