package zephyr.plugin.core.views.selector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Display;

import zephyr.plugin.core.internal.ZephyrPluginCore;
import zephyr.plugin.core.utils.Helper;

public class SelectAction<T> extends Action {
  public interface SelectedDisplay<T> {
    void select(T t);
  }

  private final SelectedDisplay<T> display;
  private final Selector<T> selector;

  public SelectAction(Selector<T> selector, SelectedDisplay<T> display) {
    super("Select...", IAction.AS_PUSH_BUTTON);
    this.display = display;
    this.selector = selector;
    setImageDescriptor(Helper.getImageDescriptor(ZephyrPluginCore.PLUGIN_ID, "icons/action_select.png"));
  }

  @Override
  public void run() {
    SelectDialogBox<T> dialogBox = new SelectDialogBox<T>(Display.getDefault().getActiveShell(), selector);
    dialogBox.open();
    T result = dialogBox.getSelection();
    if (result == null)
      return;
    T casted = selector.provider().cast(result);
    if (casted == null)
      return;
    display.select(result);
  }
}
