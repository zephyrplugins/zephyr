package zephyr.plugin.core.api;

import zephyr.plugin.core.api.codeparser.interfaces.FieldParser;
import zephyr.plugin.core.api.codeparser.parsers.CodeTreeParser;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorRegistry;
import zephyr.plugin.core.api.parsing.LabeledCollection;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Clock;

public class Zephyr {
  static public class AdvertisementInfo {
    public final Clock clock;
    public final Object advertised;
    public final String label;

    AdvertisementInfo(Clock clock, Object advertised, String label) {
      this.clock = clock;
      this.advertised = advertised;
      this.label = label;
    }
  }

  static public final Signal<AdvertisementInfo> onAdvertised = new Signal<AdvertisementInfo>();

  static public DataMonitor getSynchronizedMonitor(Clock clock) {
    return MonitorRegistry.getSynchronizedMonitor(clock);
  }

  static public void advertise(Clock clock, Object advertised) {
    advertise(clock, advertised, null);
  }

  static public void advertise(Clock clock, Object advertised, String label) {
    onAdvertised.fire(new AdvertisementInfo(clock, advertised, label));
  }

  static public void registerLabeledCollection(LabeledCollection labeledCollection, String... ids) {
    CodeTreeParser.registerLabeledCollection(labeledCollection, ids);
  }

  static public void registerParser(FieldParser parser) {
    CodeTreeParser.registerParser(parser);
  }
}
