package zephyr.plugin.common.canvas;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

public interface Painter {
  public boolean paint(Image image, GC gc);
}
