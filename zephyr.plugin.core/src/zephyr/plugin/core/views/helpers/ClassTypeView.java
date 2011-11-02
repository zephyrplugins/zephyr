package zephyr.plugin.core.views.helpers;

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
import zephyr.plugin.core.helpers.InstanceManager.InstanceListener;
import zephyr.plugin.core.helpers.SyncViewDropTarget;
import zephyr.plugin.core.views.DropTargetView;
import zephyr.plugin.core.views.ProvidedView;

public abstract class ClassTypeView<T> extends ViewPart implements ProvidedView, InstanceListener<T>, DropTargetView {
  protected final Runnable uiSetLayout = new Runnable() {
    @Override
    public void run() {
      if (!viewLock.acquire())
        return;
      if (parent.isDisposed())
        return;
      setLayout();
      parent.layout(true, true);
      viewLock.release();
    }
  };
  protected final Runnable uiUnsetLayout = new Runnable() {
    @Override
    public void run() {
      if (!viewLock.acquire())
        return;
      if (parent.isDisposed())
        return;
      unsetLayout();
      parent.layout(true, true);
      viewLock.release();
    }
  };
  protected final InstanceManager<T> instance;
  protected final ViewLock viewLock;
  protected Composite parent;
  private boolean hasBeenSynchronized = false;

  public ClassTypeView() {
    instance = new InstanceManager<T>(this);
    viewLock = new ViewLock();
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
    super.dispose();
    instance.unset();
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

  protected boolean hasBeenSynchronized() {
    return hasBeenSynchronized;
  }

  @Override
  final public boolean synchronize(Clock clock) {
    if (!viewLock.acquire())
      return false;
    boolean result = synchronize();
    hasBeenSynchronized = true;
    viewLock.release();
    return result;
  }

  @Override
  final public void repaint() {
    if (!viewLock.acquire())
      return;
    repaintView();
    viewLock.release();
  }

  @Override
  public boolean isSupported(CodeNode codeNode) {
    if (!(codeNode instanceof ClassNode))
      return false;
    return isInstanceSupported(((ClassNode) codeNode).instance());
  }

  abstract protected boolean isInstanceSupported(Object instance);

  @Override
  public void onInstanceSet() {
    hasBeenSynchronized = false;
    Display.getDefault().asyncExec(uiSetLayout);
  }

  @Override
  public void onInstanceUnset() {
    hasBeenSynchronized = false;
    Display.getDefault().asyncExec(uiUnsetLayout);
  }

  protected Clock clock() {
    return instance.clock();
  }

  protected T instance() {
    return instance.current();
  }

  abstract protected void repaintView();

  abstract protected boolean synchronize();

  abstract protected void setLayout();

  abstract protected void unsetLayout();
}
