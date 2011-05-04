package zephyr.plugin.core.api.parsing;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class Parsers {

  private static void addLabelMaps(Map<String, LabeledCollection> labelsMap, final Method method,
      final Object container) {
    LabeledCollection labeledElement = new LabeledCollection() {
      @Override
      public String label(int index) {
        try {
          return (String) method.invoke(container, index);
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        }
        return "error";
      }
    };
    LabelProvider annotation = method.getAnnotation(LabelProvider.class);
    for (String id : annotation.ids())
      labelsMap.put(id, labeledElement);
  }

  public static Map<String, LabeledCollection> buildLabelMaps(Object container) {
    Class<?> objectClass = container.getClass();
    Map<String, LabeledCollection> labelsMaps = new LinkedHashMap<String, LabeledCollection>();
    while (objectClass != null) {
      for (Method method : objectClass.getDeclaredMethods()) {
        if (!method.isAnnotationPresent(LabelProvider.class))
          continue;
        method.setAccessible(true);
        addLabelMaps(labelsMaps, method, container);
      }
      objectClass = objectClass.getSuperclass();
    }
    return labelsMaps;
  }

}
