package zephyr.plugin.core.utils;

import java.util.Arrays;
import java.util.List;



public class Misc {
  public static <T> List<T> asList(T... ts) {
    return Arrays.asList(ts);
  }

  public static boolean checkValue(double value) {
    return !Double.isInfinite(value) && !Double.isNaN(value);
  }

  public static String[] concat(String[] array01, String... array02) {
    String[] result = new String[array01.length + array02.length];
    System.arraycopy(array01, 0, result, 0, array01.length);
    System.arraycopy(array02, 0, result, array01.length, array02.length);
    return result;
  }
}
