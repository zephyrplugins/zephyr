package zephyr.plugin.core;

import java.util.Arrays;
import java.util.List;


public class Utils {
  public static <T> List<T> asList(T... ts) {
    return Arrays.asList(ts);
  }

  public static boolean checkValue(double value) {
    return !Double.isInfinite(value) && !Double.isNaN(value);
  }
}
