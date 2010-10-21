package zephyr.plugin.core.observations;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import zephyr.plugin.core.internal.observations.LineLayout;
import zephyr.plugin.core.views.TimedView;

public abstract class EnvironmentView extends ViewPart implements TimedView {
  private Composite parent;
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
    this.parent = parent;
    setToolbar(getViewSite().getActionBars().getToolBarManager());
  }

  protected void setToolbar(@SuppressWarnings("unused") IToolBarManager toolBarManager) {
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
    ObsLayout localObsLayout = getObservationLayout();
    FillLayout layout = new FillLayout(SWT.VERTICAL);
    parent.setLayout(layout);
    for (ObsWidget[] line : localObsLayout.widgetArray()) {
      if (!hasContent(line))
        continue;
      Composite lineComposite = new Composite(parent, SWT.NONE);
      lineComposite.setLayout(new LineLayout());
      for (ObsWidget widget : line)
        widget.createWidgetComposite(lineComposite);
    }
    parent.layout(true, true);
    obsLayout = localObsLayout;
  }

  static protected boolean hasContent(ObsWidget[] line) {
    boolean result = false;
    for (ObsWidget widget : line)
      result = result || widget.hasContent();
    return result;
  }

  protected void synchronize(double[] currentObservation) {
    if (obsLayout == null || currentObservation == null)
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
  public void setFocus() {
  }

  public void close() {
    obsLayout = null;
    if (parent.isDisposed())
      return;
    for (Control child : parent.getChildren())
      child.dispose();
  }
}
