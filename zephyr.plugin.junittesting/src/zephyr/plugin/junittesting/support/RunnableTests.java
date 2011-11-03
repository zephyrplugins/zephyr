package zephyr.plugin.junittesting.support;

import org.eclipse.core.runtime.IConfigurationElement;
import org.junit.Assert;

import zephyr.ZephyrCore;

public class RunnableTests {
  static public void checkRunnableAllDone() {
    Assert.assertEquals(0, ControlChecks.countChildren("zephyr.plugin.core.clocksview"));
    Assert.assertEquals(1, ControlChecks.countChildren("zephyr.plugin.core.treeview"));
    Assert.assertEquals(0, ZephyrCore.syncCode().getClockNodes().size());
  }

  static public void startRunnable(String runnableID) {
    IConfigurationElement element = ZephyrCore.findRunnable(runnableID);
    Assert.assertNotNull(element);
    ZephyrCore.start(element);
  }
}
