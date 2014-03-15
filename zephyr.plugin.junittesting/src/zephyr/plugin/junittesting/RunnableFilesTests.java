package zephyr.plugin.junittesting;

import org.junit.Before;
import org.junit.Test;
import zephyr.plugin.core.ZephyrCore;
import zephyr.plugin.junittesting.support.RunnableTests;

public class RunnableFilesTests {
  public static final long TimeOut = 120000;
  private static String zephyrRoot = "../..";

  static public void setZephyrRoot(String zephyrRoot) {
    RunnableFilesTests.zephyrRoot = zephyrRoot;
  }

  @Before
  public void before() {
    setZephyrRoot(System.getProperty("zephyr.root"));
    // ClockListener.enableVerbose();
  }

  @Test(timeout = TimeOut)
  public void testPythonLoading() {
    ZephyrCore.setSynchronous(false);
    RunnableTests.testFileLoading(zephyrRoot + "/zephyr.example.scripts/simple.py", 1000);
  }

  @Test(timeout = TimeOut)
  public void testJarLoading() {
    ZephyrCore.setSynchronous(false);
    RunnableTests.testFileLoading(zephyrRoot + "/zephyr.example.simpleclient/simpleclient.jar", 1000);
  }

  @Test(timeout = TimeOut)
  public void testClojureLoading() {
    ZephyrCore.setSynchronous(false);
    RunnableTests.testFileLoading(zephyrRoot + "/zephyr.example.scripts/simple.clj", 1000);
  }

  @Test(timeout = TimeOut)
  public void testTextFileLoading() {
    ZephyrCore.setSynchronous(true);
    RunnableTests.testFileLoading(zephyrRoot + "/zephyr.plugin.junittesting/data/textdata", 100);
  }

  @Test(timeout = TimeOut)
  public void testShortTextFileLoading() {
    ZephyrCore.setSynchronous(false);
    RunnableTests.testFileLoading(zephyrRoot + "/zephyr.plugin.junittesting/data/textdatashort", 1000);
  }
}
