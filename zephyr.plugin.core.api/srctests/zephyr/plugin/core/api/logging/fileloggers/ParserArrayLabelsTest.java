package zephyr.plugin.core.api.logging.fileloggers;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;

import zephyr.plugin.core.api.monitoring.DataLogged;
import zephyr.plugin.core.api.monitoring.LabelElementProvider;


public class ParserArrayLabelsTest {
  static protected final String[] expectedLabels = new String[] { "logged01[]", "logged01[]", "logged02[a]",
      "logged02[b]", "logged03[]data01" };

  static public class TestObjectMonitored {
    @DataLogged
    protected double data01;
  }

  @DataLogged(arrayIndexLabeled = false)
  protected final double[] logged01 = new double[2];
  @DataLogged(arrayIndexLabeled = false)
  protected final double[] logged02 = new double[2];
  @DataLogged(arrayIndexLabeled = false)
  protected final TestObjectMonitored[] logged03 = { new TestObjectMonitored() };

  @LabelElementProvider(ids = { "logged02" })
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
