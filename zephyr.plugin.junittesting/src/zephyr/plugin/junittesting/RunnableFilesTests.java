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
  public static final long TimeOut = 120000;

  @Before
  public void before() {
    // ClockListener.enableVerbose();
  }

  static public void testFileLoading(String filepath, int nbClockTick) {
    ClockListener listener = new ClockListener();
    listener.registerCondition(new NumberTickCondition(nbClockTick));
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
    ZephyrCore.setSynchronous(false);
    testFileLoading("../../zephyr/zephyr.example.simpleclient/simpleclient.jar", 1000);
  }

  @Test(timeout = TimeOut)
  public void testPythonLoading() {
    ZephyrCore.setSynchronous(false);
    testFileLoading("../../zephyr/zephyr.example.scripts/simple.py", 1000);
  }

  @Test(timeout = TimeOut)
  public void testClojureLoading() {
    ZephyrCore.setSynchronous(false);
    testFileLoading("../../zephyr/zephyr.example.scripts/simple.clj", 1000);
  }

  @Test(timeout = TimeOut)
  public void testTextFileLoading() {
    ZephyrCore.setSynchronous(true);
    testFileLoading("../../zephyr/zephyr.plugin.junittesting/data/textdata", 100);
  }

  @Test(timeout = TimeOut)
  public void testShortTextFileLoading() {
    ZephyrCore.setSynchronous(false);
    testFileLoading("../../zephyr/zephyr.plugin.junittesting/data/textdatashort", 1000);
  }
}
