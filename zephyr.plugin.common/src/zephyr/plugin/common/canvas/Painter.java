package zephyr.plugin.common.canvas;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.GC;

public interface Painter {
  public void paint(GC gc, IProgressMonitor monitor);
}
