package zephyr.plugin.core.privates.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RadioState;

import zephyr.plugin.core.internal.async.events.Event;
import zephyr.plugin.core.internal.async.listeners.EventListener;
import zephyr.plugin.core.privates.ZephyrPluginCore;
import zephyr.plugin.core.privates.synchronization.binding.ClockViews;
import zephyr.plugin.core.privates.synchronization.binding.SynchronizationMode;
import zephyr.plugin.core.privates.synchronization.binding.SynchronizationMode.Mode;

public class SetSpeedCommand extends AbstractHandler {
  static public class SynchronizationSettingLoader implements EventListener {
    @Override
    public void listen(Event eventInfo) {
      IPreferenceStore preferenceStore = ZephyrPluginCore.getDefault().getPreferenceStore();
      preferenceStore.setDefault(SynchronizationMode.SynchronousMode, SynchronizationMode.Mode.Asynchrone.ordinal());
      SynchronizationMode.Mode mode = SynchronizationMode.Mode.values()[preferenceStore
          .getInt(SynchronizationMode.SynchronousMode)];
      long delay = preferenceStore.getLong(SynchronizationMode.SynchronousDelay);
      ClockViews.synchronizationMode.setMode(mode, delay);
    }
  }

  enum ParameterValues {
    Asynchronous, Period2ms, Period10ms, Period100ms, Period1s, Synchronous
  }

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    String parameter = event.getParameter(RadioState.PARAMETER_ID);
    ParameterValues mode = ParameterValues.valueOf(parameter);
    boolean updateState = true;
    switch (mode) {
    case Asynchronous:
      ClockViews.synchronizationMode.setMode(Mode.Asynchrone, 0);
      break;
    case Period100ms:
      ClockViews.synchronizationMode.setMode(Mode.Delayed, 100);
      break;
    case Period10ms:
      ClockViews.synchronizationMode.setMode(Mode.Delayed, 10);
      break;
    case Period1s:
      ClockViews.synchronizationMode.setMode(Mode.Delayed, 1000);
      break;
    case Period2ms:
      ClockViews.synchronizationMode.setMode(Mode.Delayed, 2);
      break;
    case Synchronous:
      ClockViews.synchronizationMode.setMode(Mode.Synchrone, 0);
      break;
    default:
      System.err.println(" is an invalid parameter identifier");
      updateState = false;
    }
    if (updateState)
      HandlerUtil.updateRadioState(event.getCommand(), parameter);
    return null;
  }
}
