package zephyr.plugin.core.api.labels;


public class Labels {

  public static String label(Object object) {
    if (object instanceof Labeled)
      return ((Labeled) object).label();
    return object.toString();
  }

  public static String classLabel(Object object) {
    if (object instanceof Labeled)
      return ((Labeled) object).label();
    return object.getClass().getSimpleName();
  }
}
