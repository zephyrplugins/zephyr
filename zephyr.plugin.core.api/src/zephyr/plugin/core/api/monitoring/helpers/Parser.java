package zephyr.plugin.core.api.monitoring.helpers;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.codeparser.interfaces.CodeParser;
import zephyr.plugin.core.api.codeparser.parsers.CodeTreeParser;
import zephyr.plugin.core.api.labels.Labels;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.DataTraverser;
import zephyr.plugin.core.api.monitoring.abstracts.FieldHandler;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.monitoring.annotations.IgnoreMonitor;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.monitoring.wrappers.MonitorWrapper;
import zephyr.plugin.core.api.parsing.CollectionLabelBuilder;
import zephyr.plugin.core.api.parsing.LabeledElement;
import zephyr.plugin.core.api.parsing.Parsers;

public class Parser {
  public static final int MonitorEverythingLevel = Integer.MAX_VALUE;

  public static void findAnnotations(DataMonitor logger, Object container, List<MonitorWrapper> wrappers, int level,
      int levelRequired) {
    Class<?> objectClass = container.getClass();
    List<FieldHandler> handlers = Handlers.getFieldHandlers();
    Map<String, LabeledElement> labelsMap = Parsers.buildLabelMaps(container);
    logger.labelBuilder().pushLabelMap(labelsMap);
    boolean classIsLogged = false;
    while (objectClass != null) {
      Monitor monitor = objectClass.getAnnotation(Monitor.class);
      classIsLogged = classIsLogged || monitor != null && monitor.level() <= levelRequired;
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
    for (ArrayHandler arrayHandler : Handlers.arrayHandlers)
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
      ((MonitorContainer) child).addToMonitor(logger);
    findAnnotations(logger, child, wrappers, level, levelRequired);
  }

  public static String labelOf(Field field) {
    Monitor annotation = field.getAnnotation(Monitor.class);
    String label = annotation != null ? annotation.label() : "";
    if (label.isEmpty() && (annotation == null || !annotation.emptyLabel()))
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

  public static void parseBackup(DataMonitor dataMonitor, Object toParse, int levelRequired) {
    if (toParse instanceof Monitored)
      dataMonitor.add(Labels.label(toParse), (Monitored) toParse);
    if (toParse instanceof MonitorContainer)
      ((MonitorContainer) toParse).addToMonitor(dataMonitor);
    findAnnotations(dataMonitor, toParse, levelRequired);
  }

  public static void parse(DataMonitor dataMonitor, Object toParse, int levelRequired) {
    DataTraverser traverser = new DataTraverser(dataMonitor);
    if (toParse instanceof Monitored)
      dataMonitor.add(Labels.label(toParse), (Monitored) toParse);
    if (toParse instanceof MonitorContainer)
      ((MonitorContainer) toParse).addToMonitor(dataMonitor);
    CodeParser codeParser = new CodeTreeParser();
    ClassNode classNode = codeParser.parse(toParse);
    CodeTrees.traverse(traverser, classNode);
  }
}
