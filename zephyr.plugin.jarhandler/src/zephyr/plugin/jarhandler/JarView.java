package zephyr.plugin.jarhandler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.helpers.ClassViewProvider;
import zephyr.plugin.core.views.TimedView;
import zephyr.plugin.jarhandler.JarFileHandler.JarRunnable;

public class JarView extends ViewPart implements TimedView {
  static protected final String ViewID = "zephyr.plugin.jarhandler.jarview";

  static public class Provider extends ClassViewProvider {
    public Provider() {
      super(JarRunnable.class, ViewID);
    }
  }

  private Composite parent;
  private final List<JarComposite> composites = new ArrayList<JarComposite>();

  @Override
  public void createPartControl(Composite parent) {
    this.parent = parent;
    GridLayout parentLayout = new GridLayout(1, false);
    parentLayout.marginWidth = 0;
    parentLayout.marginHeight = 0;
    parent.setLayout(parentLayout);
    // for (JarRunnable jarRunnable : JarFileHandler.jars)
    // ZephyrSync.bind(jarRunnable.clock, this);
  }

  @Override
  public void setFocus() {
  }

  @Override
  public void synchronize() {
    for (JarComposite jarComposite : composites)
      jarComposite.synchronize();
  }

  @Override
  public void repaint() {
    if (parent == null || parent.isDisposed())
      return;
    parent.getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        refreshView();
      }
    });
  }

  protected void refreshView() {
    if (parent.isDisposed())
      return;
    if (composites.size() < JarFileHandler.jars.size())
      completeLayout();
    for (JarComposite jarComposite : composites)
      jarComposite.repaint();
  }

  protected void completeLayout() {
    for (int i = composites.size(); i < JarFileHandler.jars.size(); i++) {
      JarRunnable jar = JarFileHandler.jars.get(i);
      JarComposite jarComposite = new JarComposite(parent, jar);
      GridData griddata = new GridData(SWT.FILL, SWT.UP, true, false);
      jarComposite.setLayoutData(griddata);
      composites.add(jarComposite);
    }
    parent.layout(true, true);
  }

  @Override
  public void addTimed(Clock clock, Object drawn, Object info) {
  }

  @Override
  public boolean canAddTimed() {
    return true;
  }

  @Override
  public void dispose() {
    composites.clear();
    super.dispose();
  }
}
