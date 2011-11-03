package zephyr.plugin.core.observations;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import zephyr.plugin.core.internal.observations.LineLayout;
import zephyr.plugin.core.views.helpers.ClassTypeView;

public abstract class EnvironmentView<T> extends ClassTypeView<T> {
  ObsLayout obsLayout;
  private final Runnable repaintWidgets = new Runnable() {
    @Override
    public void run() {
      if (obsLayout == null)
        return;
      for (ObsWidget widget : obsLayout)
        widget.repaint();
    }
  };

  @Override
  public void createPartControl(Composite parent) {
    super.createPartControl(parent);
    setToolbar(getViewSite().getActionBars().getToolBarManager());
  }

  protected void setToolbar(IToolBarManager toolBarManager) {
  }

  abstract protected ObsLayout getObservationLayout();

  @Override
  protected void setLayout() {
    obsLayout = getObservationLayout();
    FillLayout layout = new FillLayout(SWT.VERTICAL);
    parent.setLayout(layout);
    for (ObsWidget[] line : obsLayout.widgetArray()) {
      if (!hasContent(line))
        continue;
      Composite lineComposite = new Composite(parent, SWT.NONE);
      lineComposite.setLayout(new LineLayout());
      for (ObsWidget widget : line)
        widget.createWidgetComposite(lineComposite);
    }
    parent.layout(true, true);
  }

  @Override
  protected void unsetLayout() {
  }

  static protected boolean hasContent(ObsWidget[] line) {
    boolean result = false;
    for (ObsWidget widget : line)
      result = result || widget.hasContent();
    return result;
  }

  protected void synchronize(double[] currentObservation) {
    if (currentObservation == null)
      return;
    for (ObsWidget widget : obsLayout)
      widget.updateValue(currentObservation);
  }

  @Override
  public void repaint() {
    if (obsLayout == null)
      return;
    parent.getDisplay().syncExec(repaintWidgets);
  }
}
