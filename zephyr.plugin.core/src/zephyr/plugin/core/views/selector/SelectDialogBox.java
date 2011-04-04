package zephyr.plugin.core.views.selector;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import zephyr.plugin.core.api.advertisement.Advertisement.Advertised;
import zephyr.plugin.core.api.labels.Labels;

public class SelectDialogBox<T> extends ElementListSelectionDialog {
  private static ILabelProvider renderer = new LabelProvider() {
    @Override
    public String getText(Object element) {
      return Labels.label(element);
    }
  };

  public SelectDialogBox(Shell parent, Selector<T> selector) {
    super(parent, renderer);
    setTitle("Selection");
    setMessage("Pattern (* = any string, ? = any char):");
    setMultipleSelection(false);
    setElements(selector.elements().toArray());
  }

  public Advertised getSelection() {
    return (Advertised) getFirstResult();
  }
}
