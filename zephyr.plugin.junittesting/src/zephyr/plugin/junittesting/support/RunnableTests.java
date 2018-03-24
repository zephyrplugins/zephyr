package zephyr.plugin.junittesting.support;

import org.junit.Assert;
import zephyr.plugin.core.RunnableFactory;
import zephyr.plugin.core.ZephyrCore;
import zephyr.plugin.core.internal.ZephyrSync;
import zephyr.plugin.core.internal.events.AtomicEvent;
import zephyr.plugin.filehandling.FileHandler;
import zephyr.plugin.junittesting.support.checklisteners.ControlChecks;
import zephyr.plugin.junittesting.support.conditions.Condition;
import zephyr.plugin.junittesting.support.conditions.NumberTickCondition;

public class RunnableTests {
  static public void checkRunnableAllDone() {
    Assert.assertEquals(0, ControlChecks.countChildren("zephyr.plugin.core.clocksview"));
    Assert.assertEquals(1, ControlChecks.countChildren("zephyr.plugin.core.treeview"));
    Assert.assertEquals(0, ZephyrSync.syncCode().getClockNodes().size());
  }

  static public void startRunnable(String elementID, Condition... conditions) {
    startRunnable(ZephyrCore.findRunnable(elementID, new String[] {}), conditions);
  }

  static public void startRunnable(RunnableFactory runnableFactory, Condition... conditions) {
    Assert.assertNotNull(runnableFactory);
    ClockListener listener = new ClockListener();
    for (Condition condition : conditions)
      listener.registerCondition(condition);
    ZephyrCore.start(runnableFactory);
    listener.waitClockRemoved();
    RunnableTests.checkRunnableAllDone();
  }

  static public void testFileLoading(String filepath, int nbClockTick) {
    ClockListener listener = new ClockListener();
    listener.registerCondition(new NumberTickCondition(nbClockTick));
    FileHandler.openFile(filepath);
    listener.waitConditions();
    listener.waitClockRemoved();
    ZephyrSync.busEvent().syncDispatch(new AtomicEvent("RunnableFilesTests"));
    checkRunnableAllDone();
  }

  public static void startRunnable(final Class<? extends Runnable> runnableClass, Condition... conditions) {
    startRunnable(new RunnableFactory() {
      @Override
      public Runnable createRunnable() {
        Runnable runnable = null;
        try {
          runnable = runnableClass.newInstance();
        } catch (InstantiationException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
        Assert.assertNotNull(runnable);
        return runnable;
      }
    }, conditions);
  }
}
