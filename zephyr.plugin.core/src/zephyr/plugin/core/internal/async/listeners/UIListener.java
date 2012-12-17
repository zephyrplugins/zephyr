package zephyr.plugin.core.internal.async.listeners;

import org.eclipse.swt.widgets.Display;
import zephyr.plugin.core.internal.async.events.Event;

public abstract class UIListener implements EventListener {
  private final boolean sync;

  protected UIListener() {
    this(false);
  }

  protected UIListener(boolean sync) {
    this.sync = sync;
  }

  @Override
  public void listen(final Event event) {
    if (sync)
      Display.getDefault().syncExec(new Runnable() {
        @Override
        public void run() {
          listenInUIThread(event);
        }
      });
    else
      Display.getDefault().asyncExec(new Runnable() {
        @Override
        public void run() {
          listenInUIThread(event);
        }
      });
  }

  abstract protected void listenInUIThread(Event event);
}
