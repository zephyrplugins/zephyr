package zephyr.plugin.core.internal.async.listeners;

import org.eclipse.swt.widgets.Display;

import zephyr.plugin.core.internal.async.events.Event;

public abstract class UIListener implements EventListener {
  @Override
  public void listen(final Event event) {
    Display.getDefault().asyncExec(new Runnable() {
      @Override
      public void run() {
        listenInUIThread(event);
      }
    });
  }

  abstract protected void listenInUIThread(Event event);
}
