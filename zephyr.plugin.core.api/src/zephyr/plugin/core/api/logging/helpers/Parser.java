package zephyr.plugin.core.api.logging.helpers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import zephyr.plugin.core.api.labels.LabeledElement;
import zephyr.plugin.core.api.logging.abstracts.FieldHandler;
import zephyr.plugin.core.api.logging.abstracts.LoggedContainer;
import zephyr.plugin.core.api.logging.abstracts.Logger;
import zephyr.plugin.core.api.logging.wrappers.MonitorWrapper;
import zephyr.plugin.core.api.monitoring.DataIgnored;
import zephyr.plugin.core.api.monitoring.DataLogged;
import zephyr.plugin.core.api.monitoring.LabelElementProvider;

public class Parser {
  static private List<ArrayHandler> arrayHandlers = new ArrayList<ArrayHandler>();
  static private List<FieldHandler> fieldHandlers = new ArrayList<FieldHandler>();

  static {
    arrayHandlers.add(new ObjectArrayHandler());
    arrayHandlers.add(new PrimitiveArrayHandler());
    addFieldHandler(new ObjectTypeHandler());
    addFieldHandler(new PrimitiveTypeHandler());
    addFieldHandler(new CollectionHandler());
    for (ArrayHandler handler : arrayHandlers)
      addFieldHandler((FieldHandler) handler);
  }

  public static void addFieldHandler(FieldHandler fieldHandler) {
    fieldHandlers.add(fieldHandler);
  }

  public static void findAnnotations(Logger logger, Object container, List<MonitorWrapper> wrappers) {
    Class<?> objectClass = container.getClass();
    List<FieldHandler> handlers = new ArrayList<FieldHandler>(fieldHandlers);
    Collections.reverse(handlers);
    Map<String, LabeledElement> labelsMap = buildLabelMaps(container);
    logger.labelBuilder().pushLabelMap(labelsMap);
    boolean classIsLogged = false;
    while (objectClass != null) {
      classIsLogged = classIsLogged || objectClass.isAnnotationPresent(DataLogged.class);
      if (objectClass.isArray())
        addElements(logger, container, wrappers);
      else
        addFields(logger, container, objectClass, classIsLogged, handlers, wrappers);
      objectClass = objectClass.getSuperclass();
    }
    logger.labelBuilder().popLabelMaps();
  }

  private static void addElements(Logger logger, Object container, List<MonitorWrapper> wrappers) {
    for (ArrayHandler arrayHandler : arrayHandlers)
      if (arrayHandler.canHandleArray(container)) {
        arrayHandler.addArray(logger, container, "", "", wrappers);
        break;
      }
  }

  private static void addFields(Logger logger, Object container, Class<?> objectClass, boolean classIsLogged,
      List<FieldHandler> handlers, List<MonitorWrapper> wrappers) {
    for (Field field : getFieldList(objectClass)) {
      if (field.isAnnotationPresent(DataIgnored.class))
        continue;
      if (!classIsLogged && !field.isAnnotationPresent(DataLogged.class))
        continue;
      field.setAccessible(true);
      for (FieldHandler fieldHandler : handlers)
        if (fieldHandler.canHandle(field, container)) {
          fieldHandler.addField(logger, container, field, wrappers);
          break;
        }
    }
  }

  private static Map<String, LabeledElement> buildLabelMaps(Object container) {
    Class<?> objectClass = container.getClass();
    Map<String, LabeledElement> labelsMaps = new LinkedHashMap<String, LabeledElement>();
    while (objectClass != null) {
      for (Method method : objectClass.getDeclaredMethods()) {
        if (!method.isAnnotationPresent(LabelElementProvider.class))
          continue;
        method.setAccessible(true);
        addLabelMaps(labelsMaps, method, container);
      }
      objectClass = objectClass.getSuperclass();
    }
    return labelsMaps;
  }

  private static void addLabelMaps(Map<String, LabeledElement> labelsMap, final Method method,
      final Object container) {
    LabeledElement labeledElement = new LabeledElement() {
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
    LabelElementProvider annotation = method.getAnnotation(LabelElementProvider.class);
    for (String id : annotation.ids())
      labelsMap.put(id, labeledElement);
  }

  private static Field[] getFieldList(Class<?> objectClass) {
    Field[] fields = objectClass.getDeclaredFields();
    Arrays.sort(fields, new Comparator<Field>() {
      @Override
      public int compare(Field o1, Field o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });
    return fields;
  }

  static protected void addChildObject(Logger logger, Object child, List<MonitorWrapper> wrappers) {
    if (child == null)
      return;
    if (child instanceof LoggedContainer)
      ((LoggedContainer) child).setLogger(logger);
    findAnnotations(logger, child, wrappers);
  }

  protected static String labelOf(Field field) {
    DataLogged annotation = field.getAnnotation(DataLogged.class);
    String label = annotation != null ? annotation.label() : "";
    if (label.isEmpty() && (annotation == null || !annotation.skipLabel()))
      label = field.getName();
    return label;
  }

  protected static String idOf(Field field) {
    DataLogged annotation = field.getAnnotation(DataLogged.class);
    String id = annotation != null ? annotation.id() : "";
    if (id.isEmpty())
      id = field.getName();
    return id;
  }

  public static String[] buildLabels(Logger logger, Field field, int size) {
    String[] labels = new String[size];
    CollectionLabelBuilder arrayLabelBuilder = new CollectionLabelBuilder(logger, field, size);
    for (int i = 0; i < labels.length; i++)
      labels[i] = arrayLabelBuilder.elementLabel(i);
    return labels;
  }
}
