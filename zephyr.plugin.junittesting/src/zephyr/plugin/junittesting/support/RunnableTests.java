package zephyr.plugin.junittesting.support;

import org.eclipse.core.runtime.IConfigurationElement;
import org.junit.Assert;

import zephyr.ZephyrCore;
import zephyr.plugin.core.async.events.Event;
import zephyr.plugin.filehandling.FileHandler;
import zephyr.plugin.junittesting.support.checklisteners.ControlChecks;
import zephyr.plugin.junittesting.support.conditions.Condition;
import zephyr.plugin.junittesting.support.conditions.NumberTickCondition;

public class RunnableTests {
  static public void checkRunnableAllDone() {
    Assert.assertEquals(0, ControlChecks.countChildren("zephyr.plugin.core.clocksview"));
    Assert.assertEquals(1, ControlChecks.countChildren("zephyr.plugin.core.treeview"));
    Assert.assertEquals(0, ZephyrCore.syncCode().getClockNodes().size());
  }

  static public void startRunnable(String runnableID, Condition... conditions) {
    ClockListener listener = new ClockListener();
    for (Condition condition : conditions)
      listener.registerCondition(condition);
    startRunnable(runnableID);
    listener.waitClockRemoved();
    RunnableTests.checkRunnableAllDone();
  }

  private static void startRunnable(String runnableID) {
    IConfigurationElement element = ZephyrCore.findRunnable(runnableID);
    Assert.assertNotNull(element);
    ZephyrCore.start(element);
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
    checkRunnableAllDone();
  }
}
