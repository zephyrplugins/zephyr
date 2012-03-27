package zephyr.plugin.junittesting.bars;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import zephyr.plugin.core.ZephyrCore;
import zephyr.plugin.junittesting.support.RunnableTests;
import zephyr.plugin.junittesting.support.checklisteners.ControlChecks;
import zephyr.plugin.junittesting.support.conditions.NumberTickCondition;

public class BarTest {
  private static final long TimeOut = 120000;

  @Before
  public void before() {
    ZephyrCore.setSynchronous(true);
  }

  class BarDrawingCondition extends NumberTickCondition {
    public BarDrawingCondition() {
      super(10);
    }

    @Override
    protected void checkConditionsWhenSatisfied() {
      Assert.assertTrue(ControlChecks.countColors(BarView.ViewID) >= 3);
    }
  }

  @Test(timeout = TimeOut)
  public void testBarDrawing() {
    RunnableTests.startRunnable("zephyr.plugin.junittesting.bars.runnable", new BarDrawingCondition());
  }
}
