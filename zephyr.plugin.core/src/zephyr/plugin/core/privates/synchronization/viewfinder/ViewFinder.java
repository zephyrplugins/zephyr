package zephyr.plugin.core.privates.synchronization.viewfinder;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;
import zephyr.plugin.core.internal.views.ProvidedView;
import zephyr.plugin.core.privates.ZephyrPluginCore;
import zephyr.plugin.core.privates.synchronization.tasks.ViewReference;
import zephyr.plugin.core.utils.Eclipse;

public class ViewFinder {
  final String viewID;
  private final List<IViewReference> existingViews;
  ProvidedView showViewResult = null;

  public ViewFinder(String viewID) {
    this.viewID = viewID;
    existingViews = createExistingViewList();
  }

  public List<IViewReference> createExistingViewList() {
    List<IViewReference> result = new ArrayList<IViewReference>();
    IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
    for (IWorkbenchWindow window : windows) {
      IWorkbenchPage[] pages = window.getPages();
      for (IWorkbenchPage page : pages) {
        for (IViewReference reference : page.getViewReferences())
          if (viewID.equals(reference.getId()))
            result.add(reference);
      }
    }
    return result;
  }

  public List<IViewReference> existingViews() {
    return existingViews;
  }

  public ViewReference showView(IViewReference reference) {
    return showView(reference.getSecondaryId(), reference.getPage());
  }

  private ViewReference showView(final String secondaryID, final IWorkbenchPage page) {
    showViewResult = null;
    Runnable bindViewRunnable = new Runnable() {
      @Override
      public void run() {
        try {
          IViewPart viewPart = page.showView(viewID, secondaryID, IWorkbenchPage.VIEW_ACTIVATE);
          if (viewPart instanceof ProvidedView)
            showViewResult = (ProvidedView) viewPart;
        } catch (PartInitException e) {
          e.printStackTrace();
        }
      }
    };
    if (Eclipse.isUIThread())
      bindViewRunnable.run();
    else
      Display.getDefault().syncExec(bindViewRunnable);
    return showViewResult != null ? ZephyrPluginCore.viewScheduler().task(showViewResult).viewRef() : null;
  }

  private ViewReference showView(final String secondaryID) {
    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    if (window == null) {
      IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
      if (windows.length == 0)
        return null;
      window = windows[0];
    }
    return showView(secondaryID, window.getActivePage());
  }

  public ViewReference provideNewView() {
    IViewRegistry viewRegistry = PlatformUI.getWorkbench().getViewRegistry();
    IViewDescriptor descriptor = viewRegistry.find(viewID);
    if (descriptor == null) {
      System.err
          .println("Zephyr warning: the view '"
              + viewID
              + "' could not be found. The value of the field 'viewid' of the provider (in zephyr.viewprovider) needs to match the field 'id' of the view (in org.eclipse.ui.views)");
      return null;
    }
    if (!descriptor.getAllowMultiple())
      return showView((String) null);
    return showView(findAvailableSecondaryID());
  }

  private String findAvailableSecondaryID() {
    int secondaryIndex = -1;
    String secondaryID = null;
    boolean viewFound = true;
    while (viewFound) {
      viewFound = false;
      secondaryIndex++;
      secondaryID = String.valueOf(secondaryIndex);
      for (IViewReference reference : existingViews) {
        if (secondaryID.equals(reference.getSecondaryId())) {
          viewFound = true;
          break;
        }
      }
    }
    return secondaryID;
  }
}
