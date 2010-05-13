package zephyr.plugin.plotting.commands;

import java.util.Collection;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import rlpark.plugin.utils.events.Listener;
import rlpark.plugin.utils.time.Clock;
import zephyr.plugin.common.ZephyrPluginCommon;
import zephyr.plugin.common.control.Control;
import zephyr.plugin.common.utils.Helper;
import zephyr.plugin.common.views.ClockViews;
import zephyr.plugin.common.views.SyncView;
import zephyr.plugin.common.views.ViewBinder;
import zephyr.plugin.plotting.graphs.PlotView;

public class SynchronizeAll extends AbstractHandler implements Listener<Control> {
  static final private String commandID = "zephyr.plugin.plotting.synchronizedplot";
  private final Command command;

  public SynchronizeAll() {
    command = Helper.getCommand(commandID);
    ZephyrPluginCommon.control().onModeChange.connect(this);
  }

  public Object execute(ExecutionEvent event) throws ExecutionException {
    final ViewBinder viewBinder = ZephyrPluginCommon.viewBinder();
    Collection<Clock> clocks = viewBinder.getClocks();
    for (Clock clock : clocks) {
      ClockViews views = viewBinder.getViews(clock);
      if (views == null)
        continue;
      for (SyncView view : views.getViews()) {
        if (view instanceof PlotView && view.synchronize())
          view.repaint();
      }
    }
    return null;
  }

  public void listen(Control control) {
    command.setHandler(isEnabled() ? this : null);
  }

  @Override
  public boolean isEnabled() {
    return ZephyrPluginCommon.control().isSuspended();
  }
}