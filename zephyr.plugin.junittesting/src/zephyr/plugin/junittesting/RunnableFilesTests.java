package zephyr.plugin.junittesting;

import org.junit.Before;
import org.junit.Test;

import zephyr.ZephyrCore;
import zephyr.plugin.core.async.events.Event;
import zephyr.plugin.filehandling.FileHandler;
import zephyr.plugin.junittesting.support.ClockListener;
import zephyr.plugin.junittesting.support.RunnableTests;
import zephyr.plugin.junittesting.support.conditions.NumberTickCondition;

public class RunnableFilesTests {
  private static final long TimeOut = 120000;

  @Before
  public void before() {
    // ClockListener.enableVerbose();
  }

  private void testFileLoading(String filepath) {
    ClockListener listener = new ClockListener();
    listener.registerCondition(new NumberTickCondition(1000));
    FileHandler.openFile(filepath);
    listener.waitConditions();
    listener.waitClockRemoved();
    ZephyrCore.busEvent().syncDispatch(new Event() {
      @Override
      public String id() {
        return "RunnableFilesTests";
      }
    });
    RunnableTests.checkRunnableAllDone();
  }

  @Test(timeout = TimeOut)
  public void testJarLoading() {
    testFileLoading("../../zephyr/zephyr.example.simpleclient/simpleclient.jar");
  }

  @Test(timeout = TimeOut)
  public void testPythonLoading() {
    testFileLoading("../../zephyr/zephyr.example.scripts/simple.py");
  }

  @Test(timeout = TimeOut)
  public void testClojureLoading() {
    testFileLoading("../../zephyr/zephyr.example.scripts/simple.clj");
  }

  @Test(timeout = TimeOut)
  public void testTextFileLoading() {
    testFileLoading("../../zephyr/zephyr.plugin.junittesting/data/textdata");
  }

  @Test(timeout = TimeOut)
  public void testShortTextFileLoading() {
    testFileLoading("../../zephyr/zephyr.plugin.junittesting/data/textdatashort");
  }
}
