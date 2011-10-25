package zephyr.plugin.junittesting.support;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import zephyr.plugin.core.views.ViewWithControl;

public class ControlChecks {
  static public int countChildren(final String viewID) {
    final int[] nbChildren = new int[] { -1 };
    Display.getDefault().syncExec(new Runnable() {
      @Override
      public void run() {
        Control control = findControl(viewID);
        nbChildren[0] = countChildrenRecursive(control);
      }
    });
    return nbChildren[0];
  }

  static public int countColors(final String viewID) {
    final Set<Integer> colors = new HashSet<Integer>();
    Display.getDefault().syncExec(new Runnable() {
      @Override
      public void run() {
        findColors(viewID, colors);
      }
    });
    return colors.size();
  }

  protected static int countChildrenRecursive(Control control) {
    if (control == null)
      return -1;
    if (control.isDisposed())
      return 0;
    if (control instanceof Tree)
      return countTreeSize((Tree) control);
    if (!(control instanceof Composite))
      return 0;
    Composite composite = (Composite) control;
    int nbChildren = composite.getChildren().length;
    for (Control child : composite.getChildren())
      nbChildren += countChildrenRecursive(child);
    return nbChildren;
  }

  private static int countTreeSize(Tree tree) {
    int nbChildren = tree.getItems().length;
    for (TreeItem treeItem : tree.getItems())
      nbChildren += countTreeSizeRecursive(treeItem);
    return nbChildren;
  }

  private static int countTreeSizeRecursive(TreeItem item) {
    int nbChildren = item.getItemCount();
    for (TreeItem treeItem : item.getItems())
      nbChildren += countTreeSizeRecursive(treeItem);
    return nbChildren;
  }

  static protected void findColors(String viewID, Set<Integer> colors) {
    Control control = findControl(viewID);
    if (control == null)
      return;
    Image image = takeScreenshot(control);
    findColors(colors, image);
  }

  static Control findControl(String viewID) {
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    IWorkbenchPage page = window.getActivePage();
    ViewWithControl part = null;
    try {
      part = (ViewWithControl) page.showView(viewID);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    Control control = part.control();
    return control;
  }

  private static void findColors(Set<Integer> colors, Image image) {
    ImageData imageData = image.getImageData();
    for (int x = 0; x < imageData.width; x++)
      for (int y = 0; y < imageData.height; y++)
        colors.add(imageData.getPixel(x, y));
  }

  private static Image takeScreenshot(Control control) {
    control.update();
    Point controlSize = control.getSize();
    GC gc = new GC(control);
    Image image = new Image(control.getDisplay(), controlSize.x, controlSize.y);
    gc.copyArea(image, 0, 0);
    gc.dispose();
    return image;
  }
}
