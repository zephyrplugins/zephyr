package zephyr.plugin.core.api.monitoring.helpers;

import java.lang.reflect.Array;
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

import zephyr.plugin.core.api.labels.CollectionLabelBuilder;
import zephyr.plugin.core.api.labels.LabeledElement;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.FieldHandler;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;
import zephyr.plugin.core.api.monitoring.annotations.IgnoreMonitor;
import zephyr.plugin.core.api.monitoring.annotations.LabelProvider;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.monitoring.wrappers.MonitorWrapper;

public class Parser {
  public static final int MonitorEverythingLevel = Integer.MAX_VALUE;

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

  public static void findAnnotations(DataMonitor logger, Object container, List<MonitorWrapper> wrappers, int level,
      int levelRequired) {
    Class<?> objectClass = container.getClass();
    List<FieldHandler> handlers = new ArrayList<FieldHandler>(fieldHandlers);
    Collections.reverse(handlers);
    Map<String, LabeledElement> labelsMap = buildLabelMaps(container);
    logger.labelBuilder().pushLabelMap(labelsMap);
    boolean classIsLogged = false;
    while (objectClass != null) {
      Monitor monitor = objectClass.getAnnotation(Monitor.class);
      classIsLogged = classIsLogged || (monitor != null && monitor.level() <= levelRequired);
      int classLevel = levelOf(level, monitor);
      if (objectClass.isArray())
        addElements(logger, container, wrappers, Loggers.isIndexIncluded(objectClass), classLevel, levelRequired);
      else
        addFields(logger, container, objectClass, classIsLogged, handlers, wrappers, classLevel, levelRequired);
      objectClass = objectClass.getSuperclass();
    }
    logger.labelBuilder().popLabelMaps();
  }

  private static int levelOf(int level, Monitor monitor) {
    return Math.max(level, monitor != null ? monitor.level() : Integer.MIN_VALUE);
  }

  private static void addElements(DataMonitor logger, Object container, List<MonitorWrapper> wrappers,
      boolean includeIndex,
      int classLevel, int levelRequired) {
    for (ArrayHandler arrayHandler : arrayHandlers)
      if (arrayHandler.canHandleArray(container)) {
        CollectionLabelBuilder labelBuilder = new CollectionLabelBuilder(logger.labelBuilder(),
                                                                         Array.getLength(container), "", "",
                                                                         includeIndex);
        arrayHandler.addArray(logger, container, labelBuilder, wrappers, classLevel, levelRequired);
        break;
      }
  }

  private static void addFields(DataMonitor logger, Object container, Class<?> objectClass, boolean classIsLogged,
      List<FieldHandler> handlers, List<MonitorWrapper> wrappers, int classLevel, int levelRequired) {
    for (Field field : getFieldList(objectClass)) {
      if (field.isSynthetic() || field.isAnnotationPresent(IgnoreMonitor.class))
        continue;
      Monitor monitor = field.getAnnotation(Monitor.class);
      if (!classIsLogged && (monitor == null || monitor.level() > levelRequired))
        continue;
      int fieldLevel = levelOf(classLevel, monitor);
      field.setAccessible(true);
      for (FieldHandler fieldHandler : handlers)
        if (fieldHandler.canHandle(field, container)) {
          fieldHandler.addField(logger, container, field, wrappers, fieldLevel, levelRequired);
          break;
        }
    }
  }

  private static Map<String, LabeledElement> buildLabelMaps(Object container) {
    Class<?> objectClass = container.getClass();
    Map<String, LabeledElement> labelsMaps = new LinkedHashMap<String, LabeledElement>();
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
    LabelProvider annotation = method.getAnnotation(LabelProvider.class);
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

  static protected void addChildObject(DataMonitor logger, Object child, List<MonitorWrapper> wrappers, int level,
      int levelRequired) {
    if (child == null)
      return;
    if (child instanceof MonitorContainer)
      ((MonitorContainer) child).addToMonitor(level, logger);
    findAnnotations(logger, child, wrappers, level, levelRequired);
  }

  public static String labelOf(Field field) {
    Monitor annotation = field.getAnnotation(Monitor.class);
    String label = annotation != null ? annotation.label() : "";
    if (label.isEmpty() && (annotation == null || !annotation.skipLabel()))
      label = field.getName();
    return label;
  }

  public static String idOf(Field field) {
    Monitor annotation = field.getAnnotation(Monitor.class);
    String id = annotation != null ? annotation.id() : "";
    if (id.isEmpty())
      id = field.getName();
    return id;
  }

  public static String[] buildLabels(DataMonitor logger, Field field, int size) {
    String[] labels = new String[size];
    CollectionLabelBuilder arrayLabelBuilder = new CollectionLabelBuilder(logger.labelBuilder(), field, size);
    for (int i = 0; i < labels.length; i++)
      labels[i] = arrayLabelBuilder.elementLabel(i);
    return labels;
  }

  public static void findAnnotations(DataMonitor logger, Object container, int levelRequired) {
    findAnnotations(logger, container, new ArrayList<MonitorWrapper>(), Integer.MIN_VALUE, levelRequired);
  }
}
