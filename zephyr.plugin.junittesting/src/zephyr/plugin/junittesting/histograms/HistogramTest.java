package zephyr.plugin.junittesting.histograms;

import junit.framework.Assert;

import org.eclipse.core.runtime.IConfigurationElement;
import org.junit.Before;
import org.junit.Test;

import zephyr.ZephyrCore;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.junittesting.support.ClockListener;
import zephyr.plugin.junittesting.support.ControlChecks;
import zephyr.plugin.junittesting.support.conditions.Condition;

public class HistogramTest {
  @Before
  public void before() {
    ZephyrCore.junitTestMode();
  }

  class HistoDrawingCondition implements Condition {
    private long time;

    @Override
    public void listen(Clock clock) {
      time = clock.timeStep();
    }

    @Override
    public boolean isSatisfied() {
      if (time < 10)
        return false;
      Assert.assertTrue(ControlChecks.countColors(HistogramView.ViewID) >= 3);
      return true;
    }
  }

  @Test
  public void testJarLoading() {
    ClockListener listener = new ClockListener();
    listener.registerCondition(new HistoDrawingCondition());
    startRunnable("zephyr.plugin.junittesting.histograms.runnable");
    listener.waitClockRemoved();
  }

  private void startRunnable(String runnableID) {
    IConfigurationElement element = ZephyrCore.findRunnable(runnableID);
    Assert.assertNotNull(element);
    ZephyrCore.start(element);
  }
}
