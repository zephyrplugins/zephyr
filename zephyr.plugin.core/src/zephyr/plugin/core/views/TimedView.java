package zephyr.plugin.core.views;

import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.synchronization.Clock;

public interface TimedView extends SyncView {
  boolean addTimed(Clock clock, CodeNode codeNode);

  void removeTimed(Clock clock);
}
