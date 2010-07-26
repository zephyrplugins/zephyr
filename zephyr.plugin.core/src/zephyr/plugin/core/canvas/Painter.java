package zephyr.plugin.core.canvas;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

public interface Painter {
  boolean paint(Image image, GC gc);

  boolean newPaintingRequired();
}
