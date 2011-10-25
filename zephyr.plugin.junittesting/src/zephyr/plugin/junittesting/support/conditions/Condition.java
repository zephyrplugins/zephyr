package zephyr.plugin.junittesting.support.conditions;

import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;

public interface Condition extends Listener<Clock> {
  boolean isSatisfied();
}
