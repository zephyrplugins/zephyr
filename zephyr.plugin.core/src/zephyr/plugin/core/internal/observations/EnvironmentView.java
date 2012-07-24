package zephyr.plugin.core.internal.observations;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.api.synchronization.Closeable;
import zephyr.plugin.core.internal.views.helpers.ClassTypeView;
import zephyr.plugin.core.privates.observations.LineLayout;

public abstract class EnvironmentView<T> extends ClassTypeView<T> implements Closeable {
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

  abstract protected ObsLayout getObservationLayout(Clock clock, T current);

  @Override
  protected void setLayout(Clock clock, T current) {
    obsLayout = getObservationLayout(clock, current);
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
    if (currentObservation == null || obsLayout == null)
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

  @Override
  public Control control() {
    return parent;
  }

  @Override
  public void close() {
    unsetInstance();
  }
}
