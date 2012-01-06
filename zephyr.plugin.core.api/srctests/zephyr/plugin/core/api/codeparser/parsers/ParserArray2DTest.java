package zephyr.plugin.core.api.codeparser.parsers;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;

import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.monitoring.fileloggers.FileLogger;


public class ParserArray2DTest {
  static final String[] expectedLabels = new String[] { "logged01Array2D/[0][0]", "logged01Array2D/[0][1]",
      "logged01Array2D/[0][2]", "logged01Array2D/[1][0]", "logged01Array2D/[1][1]", "logged01Array2D/[1][2]" };

  @Monitor
  protected final double[][] logged01Array2D = new double[2][3];

  @Test
  public void testAddLog() {
    FileLogger logger = new FileLogger(new StringWriter());
    logger.add(this);
    Assert.assertArrayEquals(expectedLabels, logger.getLabels());
  }
}
