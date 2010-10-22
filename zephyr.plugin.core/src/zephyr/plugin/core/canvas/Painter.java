package zephyr.plugin.core.canvas;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

public interface Painter {
  public interface PainterMonitor {
    void painterStep();

    boolean isCanceled();
  }

  void paint(PainterMonitor painterListener, Image image, GC gc);
}
