package zephyr.plugin.core.api;

import zephyr.plugin.core.api.synchronization.Timed;

/**
 * Just use the Runnable interface
 */
@Deprecated
public interface ZephyrRunnable extends Runnable, Timed {
}
