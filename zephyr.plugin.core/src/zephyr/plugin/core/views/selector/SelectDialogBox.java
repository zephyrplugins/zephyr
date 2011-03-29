package zephyr.plugin.core.views.selector;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

public class SelectDialogBox<T> extends ElementListSelectionDialog {
  private static ILabelProvider renderer = new LabelProvider();

  public SelectDialogBox(Shell parent, Selector<T> selector) {
    super(parent, renderer);
    setTitle("Selection");
    setMessage("Pattern (* = any string, ? = any char):");
    setMultipleSelection(false);
    setElements(selector.elements());
  }

  @SuppressWarnings("unchecked")
  public T getSelection() {
    return (T) getFirstResult();
  }
}
