package zephyr.plugin.junittesting;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import zephyr.plugin.filehandling.FileHandler;
import zephyr.plugin.junittesting.support.ClockListener;
import zephyr.plugin.junittesting.support.ControlChecks;
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
    checkViews(listener);
  }

  private void checkViews(ClockListener clockListener) {
    clockListener.waitClockRemoved();
    Assert.assertEquals(0, ControlChecks.countChildren("zephyr.plugin.core.clocksview"));
    Assert.assertEquals(1, ControlChecks.countChildren("zephyr.plugin.core.treeview"));
  }

  @Test(timeout = TimeOut)
  public void testJarLoading() {
    testFileLoading("../zephyr.example.simpleclient/simpleclient.jar");
  }

  @Test(timeout = TimeOut)
  public void testPythonLoading() {
    testFileLoading("../zephyr.example.scripts/simple.py");
  }

  @Test(timeout = TimeOut)
  public void testClojureLoading() {
    testFileLoading("../zephyr.example.scripts/simple.clj");
  }
}
