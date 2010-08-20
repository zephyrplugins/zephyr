package zephyr.plugin.core.api.logging.fileloggers;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;

import zephyr.plugin.core.api.monitoring.DataLogged;


public class ParserArray2DTest {
  static protected final String[] expectedLabels = new String[] { "logged01Array2D[0][0]", "logged01Array2D[0][1]",
      "logged01Array2D[0][2]", "logged01Array2D[1][0]", "logged01Array2D[1][1]", "logged01Array2D[1][2]" };

  @DataLogged
  protected final double[][] logged01Array2D = new double[2][3];

  @Test
  public void testAddLog() {
    FileLogger logger = new FileLogger(new StringWriter());
    logger.add(this);
    Assert.assertArrayEquals(expectedLabels, logger.getLabels());
  }
}
