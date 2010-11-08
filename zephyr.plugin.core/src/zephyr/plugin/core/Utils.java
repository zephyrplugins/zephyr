package zephyr.plugin.core;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.widgets.Display;


public class Utils {
  static public boolean isUIThread() {
    return Display.findDisplay(Thread.currentThread()) != null;
  }

  public static <T> List<T> asList(T... ts) {
    return Arrays.asList(ts);
  }

  public static boolean checkValue(double value) {
    return !Double.isInfinite(value) && !Double.isNaN(value);
  }
}
