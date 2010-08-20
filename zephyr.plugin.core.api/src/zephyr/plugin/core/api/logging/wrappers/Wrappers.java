package zephyr.plugin.core.api.logging.wrappers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zephyr.plugin.core.api.labels.Labeled;
import zephyr.plugin.core.api.monitoring.DataLogged;

public class Wrappers {
  static final Map<String, MonitorWrapper> wrappers = new HashMap<String, MonitorWrapper>();
  static {
    registerWrapper(Squared.ID, new Squared());
    registerWrapper(Abs.ID, new Abs());
  }

  private Wrappers() {
  }

  public static void registerWrapper(String id, MonitorWrapper wrapper) {
    wrappers.put(id, wrapper);
  }

  public static List<MonitorWrapper> getWrappers(Field field, List<MonitorWrapper> parentWrappers) {
    DataLogged monitorAnnotation = field.getAnnotation(DataLogged.class);
    if (monitorAnnotation == null)
      return parentWrappers;
    List<MonitorWrapper> result = new ArrayList<MonitorWrapper>(parentWrappers);
    String[] wrapperIDs = monitorAnnotation.wrappers();
    for (String id : wrapperIDs) {
      MonitorWrapper wrapper = wrappers.get(id);
      if (wrapper != null)
        result.add(wrapper);
    }
    return result;
  }

  public static String wrapperLabel(MonitorWrapper wrapper) {
    if (wrapper instanceof Labeled)
      return ((Labeled) wrapper).label();
    return wrapper.getClass().getSimpleName();
  }
}
