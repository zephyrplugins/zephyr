package zephyr.plugin.core.observations;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import zephyr.plugin.core.internal.observations.LineLayout;
import zephyr.plugin.core.views.TimedView;

public abstract class EnvironmentView extends ViewPart implements TimedView {
  private Composite parent;
  private ObsLayout obsLayout;

  @Override
  public void createPartControl(Composite parent) {
    this.parent = parent;
  }

  abstract protected ObsLayout getObservationLayout();

  public void createLayout() {
    Display.getDefault().asyncExec(new Runnable() {
      @Override
      public void run() {
        createObservationLayout();
      }
    });
  }

  protected void createObservationLayout() {
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

  static protected boolean hasContent(ObsWidget[] line) {
    boolean result = false;
    for (ObsWidget widget : line)
      result = result || widget.hasContent();
    return result;
  }

  protected boolean synchronize(double[] currentObservation) {
    if (obsLayout == null)
      return false;
    for (ObsWidget widget : obsLayout)
      widget.updateValue(currentObservation);
    return true;
  }

  @Override
  public void repaint() {
    for (ObsWidget widget : obsLayout)
      widget.repaint();
  }

  @Override
  public boolean isDisposed() {
    return parent.isDisposed();
  }

  @Override
  public void setFocus() {
  }
}
