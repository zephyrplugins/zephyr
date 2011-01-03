package zephyr.plugin.core.api.logfiles;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;


public abstract class LogFileTest {
  @Test
  public void testHasNext() throws IOException {
    LogFile logFile = LogFile.load(getUnitTestLogFile());
    Assert.assertTrue(!logFile.eof());
    int timeStep = 0;
    while (!logFile.eof()) {
      logFile.step();
      timeStep++;
    }
    Assert.assertEquals(5, timeStep);
  }

  @Test
  public void testLegend() throws IOException {
    LogFile logFile = LogFile.load(getUnitTestLogFile());
    String[] labels = logFile.labels();
    Assert.assertEquals("Time", labels[0]);
    Assert.assertEquals("Command1", labels[1]);
    Assert.assertEquals("Data1", labels[2]);
  }

  @Test
  public void testParseLine() throws IOException {
    LogFile logFile = LogFile.load(getUnitTestLogFile());
    logFile.step();
    final double[] expectedObservation01 = { 22000.0, 1.0, 2.0, 3.0, 4.0, 5.0 };
    Assert.assertTrue(Arrays.equals(expectedObservation01, logFile.currentLine()));
    logFile.step();
    final double[] expectedObservation02 = { 22010.0, 2.0, 2.0, 3.0, 4.0, 5.0 };
    Assert.assertTrue(Arrays.equals(expectedObservation02, logFile.currentLine()));
  }

  public static void compareArray(Object[] expected, Object[] actual) {
    for (int i = 0; i < expected.length; i++)
      Assert.assertEquals(expected[i], actual[i]);
  }

  abstract String getUnitTestLogFile() throws IOException;

  static String getParkPath() {
    File current = new File("");
    while (!new File(current.getAbsolutePath() + "/.git").canRead())
      current = new File(current.getAbsolutePath() + "/..");
    return current.getAbsolutePath();
  }

  static String getDataPath(String projectFolder) {
    File dataFolder = new File(String.format("%s/%s/data", getParkPath(), projectFolder));
    assert dataFolder.canRead();
    return dataFolder.getAbsolutePath();
  }

  public static String getDataPath(String projectFolder, String filename) {
    String dataFolder = getDataPath(projectFolder);
    File dataFile = new File(dataFolder + "/" + filename);
    assert dataFile.canRead();
    return dataFile.getAbsolutePath();
  }
}
