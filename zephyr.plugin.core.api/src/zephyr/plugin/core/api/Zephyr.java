package zephyr.plugin.core.api;

import zephyr.plugin.core.api.codeparser.interfaces.FieldParser;
import zephyr.plugin.core.api.codeparser.parsers.CodeTreeParser;
import zephyr.plugin.core.api.parsing.LabeledCollection;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Clock;

public class Zephyr {
  static public class AdvertisementInfo {
    public final Clock clock;
    public final Object advertised;

    AdvertisementInfo(Clock clock, Object advertised) {
      this.clock = clock;
      this.advertised = advertised;
    }
  }

  static public final Signal<AdvertisementInfo> onAdvertised = new Signal<AdvertisementInfo>();

  static public void advertise(Clock clock, Object advertised) {
    onAdvertised.fire(new AdvertisementInfo(clock, advertised));
  }

  static public void registerLabeledCollection(LabeledCollection labeledCollection, String... ids) {
    CodeTreeParser.registerLabeledCollection(labeledCollection, ids);
  }

  static public void registerParser(FieldParser parser) {
    CodeTreeParser.registerParser(parser);
  }
}
