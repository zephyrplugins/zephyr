package zephyr.plugin.plotting.bar2d;

import org.eclipse.swt.graphics.RGB;

public interface BarColorMap {
  RGB toColor(int x, double value);
}
