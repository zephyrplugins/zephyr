package zephyr.plugin.junittesting;

import org.junit.Before;
import org.junit.Test;

import zephyr.ZephyrCore;
import zephyr.plugin.junittesting.support.RunnableTests;

public class RunnableFilesTests {
  public static final long TimeOut = 120000;

  @Before
  public void before() {
    // ClockListener.enableVerbose();
  }

  @Test(timeout = TimeOut)
  public void testJarLoading() {
    ZephyrCore.setSynchronous(false);
    RunnableTests.testFileLoading("../../zephyr/zephyr.example.simpleclient/simpleclient.jar", 1000);
  }

  @Test(timeout = TimeOut)
  public void testPythonLoading() {
    ZephyrCore.setSynchronous(false);
    RunnableTests.testFileLoading("../../zephyr/zephyr.example.scripts/simple.py", 1000);
  }

  @Test(timeout = TimeOut)
  public void testClojureLoading() {
    ZephyrCore.setSynchronous(false);
    RunnableTests.testFileLoading("../../zephyr/zephyr.example.scripts/simple.clj", 1000);
  }

  @Test(timeout = TimeOut)
  public void testTextFileLoading() {
    ZephyrCore.setSynchronous(true);
    RunnableTests.testFileLoading("../../zephyr/zephyr.plugin.junittesting/data/textdata", 100);
  }

  @Test(timeout = TimeOut)
  public void testShortTextFileLoading() {
    ZephyrCore.setSynchronous(false);
    RunnableTests.testFileLoading("../../zephyr/zephyr.plugin.junittesting/data/textdatashort", 1000);
  }
}
