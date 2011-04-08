package zephyr.plugin.tests.treeview;

import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.advertisement.Advertisement;
import zephyr.plugin.core.api.advertisement.Advertisement.Advertised;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.startup.StartupJob;
import zephyr.plugin.tests.ZephyrTestsPlugin;

public class RegisterToAdvertize implements StartupJob {
  @Override
  public int level() {
    return 10;
  }

  @Override
  public void run() {
    Zephyr.advertisement().onAdvertiseRoot.connect(new Listener<Advertisement.Advertised>() {
      @Override
      public void listen(Advertised eventInfo) {
        ZephyrTestsPlugin.codeParser().parse(eventInfo.clock, eventInfo.advertised);
      }
    });
  }
}
