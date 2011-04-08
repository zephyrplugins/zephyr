package zephyr.plugin.core.views.selector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Display;

import zephyr.ZephyrCore;
import zephyr.plugin.core.api.advertisement.Advertisement.Advertised;
import zephyr.plugin.core.api.advertisement.DataInfo;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.utils.Helper;

public class SelectAction<T> extends Action {
  public interface SelectedDisplay<T> {
    void select(Clock clock, T t, DataInfo info);
  }

  private final SelectedDisplay<T> display;
  private final Selector<T> selector;

  public SelectAction(Selector<T> selector, SelectedDisplay<T> display) {
    super("Select...", IAction.AS_PUSH_BUTTON);
    this.display = display;
    this.selector = selector;
    setImageDescriptor(Helper.getImageDescriptor(ZephyrCore.PluginID, "icons/action_select.png"));
  }

  @Override
  public void run() {
    SelectDialogBox<T> dialogBox = new SelectDialogBox<T>(Display.getDefault().getActiveShell(), selector);
    dialogBox.open();
    Advertised result = dialogBox.getSelection();
    if (result == null)
      return;
    selector.selectElement(display, result);
  }
}
