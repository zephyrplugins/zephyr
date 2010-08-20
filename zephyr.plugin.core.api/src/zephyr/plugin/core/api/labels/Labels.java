package zephyr.plugin.core.api.labels;

public class Labels {

  public static String label(Object object) {
    if (object instanceof Labeled)
      return ((Labeled) object).label();
    return object.toString();
  }

  public static String collectionPattern(String collectionLabel, int size, boolean includeIndex) {
    if (!includeIndex)
      return collectionLabel + "[";
    if (size < 10)
      return String.format("%s[%%d", collectionLabel);
    return String.format("%s[%%0%dd", collectionLabel, (int) Math.ceil(Math.log10(size)));
  }

  public static String collectionLabel(String collectionPattern, int index, String suffix, boolean includeIndex) {
    StringBuilder result = (includeIndex ?
        new StringBuilder(String.format(collectionPattern, index)) :
        new StringBuilder(collectionPattern));
    if (suffix != null)
      result.append(suffix);
    result.append("]");
    return result.toString();
  }

  public static String classLabel(Object object) {
    if (object instanceof Labeled)
      return ((Labeled) object).label();
    return object.getClass().getSimpleName();
  }
}
