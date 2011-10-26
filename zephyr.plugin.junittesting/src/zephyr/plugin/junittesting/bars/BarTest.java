package zephyr.plugin.junittesting.bars;

import junit.framework.Assert;

import org.eclipse.core.runtime.IConfigurationElement;
import org.junit.Before;
import org.junit.Test;

import zephyr.ZephyrCore;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.junittesting.support.ClockListener;
import zephyr.plugin.junittesting.support.ControlChecks;
import zephyr.plugin.junittesting.support.conditions.Condition;

public class BarTest {
  private static final long TimeOut = 120000;

  @Before
  public void before() {
    ZephyrCore.junitTestMode();
  }

  class BarDrawingCondition implements Condition {
    private long time;

    @Override
    public void listen(Clock clock) {
      time = clock.timeStep();
    }

    @Override
    public boolean isSatisfied() {
      if (time < 10)
        return false;
      Assert.assertTrue(ControlChecks.countColors(BarView.ViewID) >= 3);
      return true;
    }
  }

  @Test(timeout = TimeOut)
  public void testBarDrawing() {
    ClockListener listener = new ClockListener();
    listener.registerCondition(new BarDrawingCondition());
    startRunnable("zephyr.plugin.junittesting.bars.runnable");
    listener.waitClockRemoved();
  }

  private void startRunnable(String runnableID) {
    IConfigurationElement element = ZephyrCore.findRunnable(runnableID);
    Assert.assertNotNull(element);
    ZephyrCore.start(element);
  }
}
