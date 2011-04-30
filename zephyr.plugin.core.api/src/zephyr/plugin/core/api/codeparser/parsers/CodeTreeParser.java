package zephyr.plugin.core.api.codeparser.parsers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.codeparser.codetree.PrimitiveNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeParser;
import zephyr.plugin.core.api.codeparser.interfaces.FieldParser;
import zephyr.plugin.core.api.codeparser.interfaces.MutableParentNode;
import zephyr.plugin.core.api.codeparser.interfaces.ParentNode;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.monitoring.annotations.IgnoreMonitor;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.parsing.CollectionLabelBuilder;
import zephyr.plugin.core.api.parsing.LabelProvider;
import zephyr.plugin.core.api.parsing.LabeledElement;

public class CodeTreeParser implements CodeParser {
  static private LinkedList<FieldParser> parsers = new LinkedList<FieldParser>();
  private final Stack<Map<String, LabeledElement>> labelsMapStack = new Stack<Map<String, LabeledElement>>();

  static {
    registerParser(new ObjectParser());
    registerParser(new ObjectCollectionParser());
    registerParser(new ObjectArrayParser());
    registerParser(new PrimitiveArrayParser());
    registerParser(new PrimitiveCollectionParser());
    registerParser(new PrimitiveParser());
  }

  static private Field[] getFieldList(Class<?> objectClass) {
    Field[] fields = objectClass.getDeclaredFields();
    Arrays.sort(fields, new Comparator<Field>() {
      @Override
      public int compare(Field o1, Field o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });
    return fields;
  }

  static private void addLabelMaps(Map<String, LabeledElement> labelsMap, final Method method, final Object container) {
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

  static private Map<String, LabeledElement> buildLabelMaps(Object container) {
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

  private boolean isMonitored(ClassNode classNode, Field field) {
    if (field.isSynthetic() || field.isAnnotationPresent(IgnoreMonitor.class))
      return false;
    if (classNode.isClassAnnotated())
      return true;
    if (field.isAnnotationPresent(Monitor.class))
      return true;
    return false;
  }

  @Override
  public void recursiveParseInstance(MutableParentNode parentNode, Field field, Object fieldValue) {
    for (FieldParser parser : parsers) {
      if (parser.canParse(fieldValue)) {
        parser.parse(this, parentNode, field, fieldValue);
        return;
      }
    }
  }

  @Override
  public void recursiveParseClass(final ClassNode classNode, Object container) {
    labelsMapStack.push(buildLabelMaps(container));
    Class<?> objectClass = container.getClass();
    while (objectClass != null) {
      Field[] fields = getFieldList(objectClass);
      for (Field field : fields) {
        if (field.getName().equals("serialVersionUID"))
          continue;
        field.setAccessible(true);
        if (isMonitored(classNode, field)) {
          Object fieldValue = CodeTrees.getValueFromField(field, classNode.instance());
          if (fieldValue != null)
            recursiveParseInstance(classNode, field, fieldValue);
        }
      }
      objectClass = objectClass.getSuperclass();
    }
    if (container instanceof MonitorContainer)
      ((MonitorContainer) container).addToMonitor(new DataMonitor() {
        @Override
        public void add(String label, int level, Monitored monitored) {
          PrimitiveNode child = new PrimitiveNode(label, classNode, monitored, level);
          classNode.addChild(child);
        }
      });
    labelsMapStack.pop();
  }

  @Override
  public ClassNode parse(Object root) {
    return parse(null, root);
  }

  @Override
  public ClassNode parse(ParentNode parent, Object root) {
    ClassNode classNode = new ClassNode("", parent, root, null);
    recursiveParseClass(classNode, classNode.instance());
    return classNode;
  }

  static public void registerParser(FieldParser parser) {
    parsers.addFirst(parser);
  }

  private LabeledElement getLabeledElement(String id) {
    for (Map<String, LabeledElement> labelsMap : labelsMapStack) {
      LabeledElement labeledElement = labelsMap.get(id);
      if (labeledElement != null)
        return labeledElement;
    }
    return null;
  }

  @Override
  public CollectionLabelBuilder newCollectionLabelBuilder(Field field, int length) {
    String id = "";
    boolean includeIndex = true;
    if (field != null && field.isAnnotationPresent(Monitor.class)) {
      Monitor annotation = field.getAnnotation(Monitor.class);
      id = annotation.id();
      includeIndex = annotation.arrayIndexLabeled();
    }
    if (field != null && id.isEmpty())
      id = field.getName();
    return new CollectionLabelBuilder(getLabeledElement(id), ":", length, includeIndex);
  }
}
