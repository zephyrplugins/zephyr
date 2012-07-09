package zephyr.plugin.core.privates.observations;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

public class LineLayout extends Layout {
  public static final int MARGIN = 0;
  public static final int SPACING = 0;

  private Point[] sizes;
  private int prefWidth, prefHeight, fixedWidth;

  @Override
  protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
    Control children[] = composite.getChildren();
    if (flushCache || sizes == null || sizes.length != children.length)
      initialize(children);
    int width = wHint, height = hHint;
    if (wHint == SWT.DEFAULT)
      width = prefWidth;
    if (hHint == SWT.DEFAULT)
      height = prefHeight;
    return new Point(width + 2 * MARGIN, height + 2 * MARGIN);
  }

  @Override
  protected void layout(Composite composite, boolean flushCache) {
    Control children[] = composite.getChildren();
    if (flushCache || sizes == null || sizes.length != children.length)
      initialize(children);
    Rectangle rect = composite.getClientArea();
    int x = MARGIN;
    final int y = MARGIN;
    final int height = rect.height - 2 * MARGIN;
    float widthRatio = computeWidthRatio(children, rect);
    for (int i = 0; i < children.length; i++) {
      int width = isResizable(children[i]) ? (int) (sizes[i].x * widthRatio) : sizes[i].x;
      children[i].setBounds(x, y, width, height);
      x += width + SPACING;
    }
  }

  private static boolean isResizable(Control control) {
    LineData lineData = (LineData) control.getLayoutData();
    if (lineData == null)
      return true;
    return lineData.resizable;
  }

  private float computeWidthRatio(Control[] children, Rectangle rect) {
    return (rect.width - fixedWidth - (children.length - 1) * SPACING - 2 * MARGIN) / (float) (prefWidth - fixedWidth);
  }

  void initialize(Control children[]) {
    prefWidth = 0;
    prefHeight = 0;
    sizes = new Point[children.length];
    for (int i = 0; i < children.length; i++) {
      sizes[i] = children[i].computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
      prefHeight = Math.max(prefHeight, sizes[i].y);
      prefWidth += sizes[i].x;
      if (!isResizable(children[i]))
        fixedWidth += sizes[i].x;
    }
    prefWidth += (children.length - 1) * SPACING;
  }
}
