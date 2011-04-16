package zephyr.plugin.core.views;

import zephyr.plugin.core.api.synchronization.Clock;

public interface SyncView {
  /**
   * Copy the data the from the model to the view
   * 
   * @param clock
   *          the clock used by the model
   * 
   * @return true if some data has been synchronised
   */
  boolean synchronize(Clock clock);

  /**
   * Repaint this view
   */
  void repaint();
}
