package zephyr.plugin.core.views.helpers;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;

import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.helpers.InstanceManager;
import zephyr.plugin.core.helpers.InstanceManager.InstanceListener;
import zephyr.plugin.core.helpers.SyncViewDropTarget;
import zephyr.plugin.core.views.DropTargetView;
import zephyr.plugin.core.views.ProvidedView;
import zephyr.plugin.core.views.ViewWithControl;

public abstract class ClassTypeView<T> extends ViewPart implements ProvidedView, InstanceListener<T>, DropTargetView,
    ViewWithControl {
  protected final Runnable uiSetLayout = new Runnable() {
    @Override
    public void run() {
      if (parent.isDisposed() || instance.current() == null)
        return;
      if (!viewLock.acquire())
        return;
      setViewName();
      setLayout();
      parent.layout(true, true);
      viewLock.release();
      isLayoutReady = true;
    }
  };
  protected final Runnable uiUnsetLayout = new Runnable() {
    @Override
    public void run() {
      if (parent.isDisposed())
        return;
      if (!viewLock.acquire())
        return;
      setViewName();
      unsetLayout();
      parent.layout(true, true);
      viewLock.release();
    }
  };
  protected final InstanceManager<T> instance;
  protected final ViewLock viewLock;
  protected Composite parent;
  protected boolean hasBeenSynchronized = false;
  protected boolean isLayoutReady = false;
  private Color backgroundColor;

  public ClassTypeView() {
    instance = new InstanceManager<T>(this);
    viewLock = new ViewLock();
  }

  @Override
  public void createPartControl(Composite parent) {
    this.parent = parent;
    backgroundColor = parent.getBackground();
    new SyncViewDropTarget(this, parent);
    setDefaultName();
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

  @SuppressWarnings("unchecked")
  protected void setViewName() {
    CodeNode codeNode = instance.codeNode();
    if (codeNode == null) {
      setDefaultName();
      return;
    }
    setViewName(codeNode, (T) ((ClassNode) codeNode).instance());
  }

  protected void defaultPainting(GC gc) {
    gc.setBackground(backgroundColor);
    gc.fillRectangle(gc.getClipping());
  }

  protected void setViewName(CodeNode codeNode, T instance) {
    setViewName(codeNode.label(), codeNode.path());
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
    if (!isLayoutReady)
      return false;
    if (!viewLock.tryAcquire())
      return false;
    boolean result = synchronize();
    hasBeenSynchronized = true;
    viewLock.release();
    unprotectedSynchronization();
    return result;
  }

  protected void unprotectedSynchronization() {
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
    isLayoutReady = false;
    Display.getDefault().asyncExec(uiUnsetLayout);
  }

  protected void setDefaultName() {
    IViewRegistry viewRegistry = PlatformUI.getWorkbench().getViewRegistry();
    IViewDescriptor descriptor = viewRegistry.find(getSite().getId());
    setViewName(descriptor.getLabel(), "");
  }

  @Override
  public Control control() {
    return parent;
  }

  abstract protected boolean synchronize();

  abstract protected void setLayout();

  abstract protected void unsetLayout();
}
