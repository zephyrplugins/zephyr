package zephyr.plugin.core.views.helpers;

import java.util.concurrent.Semaphore;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.helpers.InstanceManager;
import zephyr.plugin.core.helpers.InstanceManager.SetableView;
import zephyr.plugin.core.helpers.SyncViewDropTarget;
import zephyr.plugin.core.views.DropTargetView;
import zephyr.plugin.core.views.TimedView;

public abstract class ClassTypeView<T> extends ViewPart implements TimedView, SetableView, DropTargetView {
  private final Semaphore viewLock = new Semaphore(1, true);
  protected final InstanceManager<T> instance;
  protected Composite parent;

  public ClassTypeView() {
    instance = new InstanceManager<T>(this);
  }

  @Override
  public void createPartControl(Composite parent) {
    this.parent = parent;
    new SyncViewDropTarget(this, parent);
  }

  @Override
  final public void drop(CodeNode[] supported) {
    instance.drop(supported);
  }

  @Override
  public void setFocus() {
  }

  @Override
  final public boolean[] provide(CodeNode[] codeNode) {
    return instance.provide(codeNode);
  }

  @Override
  public void init(IViewSite site, IMemento memento) throws PartInitException {
    super.init(site, memento);
    instance.parseMemento(memento);
  }

  @Override
  public void saveState(IMemento memento) {
    super.saveState(memento);
    instance.saveState(memento);
  }

  @Override
  public void dispose() {
    instance.unset();
    super.dispose();
  }

  protected void setViewName() {
    if (instance.isNull())
      return;
    setViewName(instance.codeNode().label(), instance.codeNode().path());
  }

  protected void setViewName(final String viewName, final String toolTip) {
    Display.getDefault().asyncExec(new Runnable() {
      @SuppressWarnings("synthetic-access")
      @Override
      public void run() {
        setPartName(viewName);
        setTitleToolTip(toolTip);
        firePropertyChange(org.eclipse.ui.IWorkbenchPart.PROP_TITLE);
        if (parent != null && !parent.isDisposed())
          parent.redraw();
      }
    });
  }

  private void releaseViewLock() {
    viewLock.release();
  }

  private void acquireViewLock() {
    try {
      viewLock.acquire();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  final public void setInstance() {
    acquireViewLock();
    set(instance.current());
    releaseViewLock();
  }

  @Override
  final public boolean synchronize(Clock clock) {
    acquireViewLock();
    boolean result = synchronize();
    releaseViewLock();
    return result;
  }

  @Override
  final public void repaint() {
    acquireViewLock();
    repaintView();
    releaseViewLock();
  }

  @Override
  final public void unsetInstance() {
    acquireViewLock();
    unset();
    releaseViewLock();
  }

  @Override
  public boolean isSupported(CodeNode codeNode) {
    if (!(codeNode instanceof ClassNode))
      return false;
    return classSupported().isInstance(((ClassNode) codeNode).instance());
  }

  protected Class<?> classSupported() {
    return Object.class;
  }

  abstract protected void set(T current);

  abstract protected void unset();

  abstract protected void repaintView();

  abstract protected boolean synchronize();
}
