package zephyr.plugin.core.internal.views.helpers;

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
import zephyr.plugin.core.api.internal.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.internal.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.ZephyrSync;
import zephyr.plugin.core.internal.helpers.InstanceManager;
import zephyr.plugin.core.internal.helpers.InstanceManager.InstanceListener;
import zephyr.plugin.core.internal.helpers.SyncViewDropTarget;
import zephyr.plugin.core.internal.views.DropTargetView;
import zephyr.plugin.core.internal.views.ProvidedView;
import zephyr.plugin.core.internal.views.ViewWithControl;

public abstract class ClassTypeView<T> extends ViewPart implements ProvidedView, InstanceListener<T>, DropTargetView,
    ViewWithControl {
  protected final Runnable uiSetLayout = new Runnable() {
    @Override
    @SuppressWarnings("synthetic-access")
    public void run() {
      Clock clock = instance.clock();
      T current = instance.current();
      if (parent.isDisposed() || clock == null || current == null)
        return;
      if (!viewLock.acquire())
        return;
      setLayout(clock, current);
      parent.layout(true, true);
      viewLock.release();
      isLayoutReady = true;
      ZephyrSync.submitView(ClassTypeView.this, clock);
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
  private final InstanceManager<T> instance;
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
    if (instance.current() == null)
      setDefaultName();
  }

  @Override
  public void drop(CodeNode[] supported) {
    instance.drop(supported);
  }

  @Override
  public void setFocus() {
  }

  @Override
  public boolean[] provide(CodeNode[] codeNode) {
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
    setViewName(codeNode.label(), CodeTrees.mergePath(codeNode.path()));
  }

  public void setViewName(final String viewName, final String toolTip) {
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
    T current = instance.current();
    boolean result = lockAndSynchronize(clock, current);
    viewLock.release();
    unprotectedSynchronization(current);
    return result;
  }

  private boolean lockAndSynchronize(Clock clock, T current) {
    if (clock == null || current == null)
      return false;
    boolean result = false;
    try {
      result = synchronize(current);
      hasBeenSynchronized = true;
    } catch (Throwable t) {
      t.printStackTrace();
    }
    return result;
  }

  protected void unprotectedSynchronization(T current) {
  }

  @Override
  public boolean isSupported(CodeNode codeNode) {
    if (!(codeNode instanceof ClassNode))
      return false;
    return isInstanceSupported(((ClassNode) codeNode).instance());
  }

  abstract protected boolean isInstanceSupported(Object instance);

  @Override
  public void onInstanceSet(Clock clock, T current) {
    hasBeenSynchronized = false;
    Display.getDefault().asyncExec(uiSetLayout);
  }

  @Override
  public void onInstanceUnset(Clock clock) {
    hasBeenSynchronized = false;
    isLayoutReady = false;
    Display.getDefault().asyncExec(uiUnsetLayout);
  }

  public void setDefaultName() {
    IViewRegistry viewRegistry = PlatformUI.getWorkbench().getViewRegistry();
    IViewDescriptor descriptor = viewRegistry.find(getSite().getId());
    setViewName(descriptor.getLabel(), "");
  }

  @Override
  public Control control() {
    return parent;
  }

  protected void unsetInstance() {
    instance.unset();
  }


  protected CodeNode codeNode() {
    return instance.codeNode();
  }

  abstract protected boolean synchronize(T current);

  abstract protected void setLayout(Clock clock, T current);

  abstract protected void unsetLayout();
}
