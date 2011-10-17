package zephyr.plugin.junittesting.conditions;

import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;

public interface Condition extends Listener<Clock> {
  boolean isSatisfied();
}
