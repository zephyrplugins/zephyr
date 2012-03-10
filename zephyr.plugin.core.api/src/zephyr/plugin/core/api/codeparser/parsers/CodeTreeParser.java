package zephyr.plugin.core.api.codeparser.parsers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.codeparser.codetree.PrimitiveNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeParser;
import zephyr.plugin.core.api.codeparser.interfaces.FieldParser;
import zephyr.plugin.core.api.codeparser.interfaces.MutableParentNode;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.monitoring.annotations.IgnoreMonitor;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.parsing.CollectionLabelBuilder;
import zephyr.plugin.core.api.parsing.LabelProvider;
import zephyr.plugin.core.api.parsing.LabeledCollection;

public class CodeTreeParser implements CodeParser {
  static private LinkedList<FieldParser> parsers = new LinkedList<FieldParser>();
  static private Stack<Map<String, LabeledCollection>> staticLabelsMapStack = new Stack<Map<String, LabeledCollection>>();

  static {
    registerParser(new ObjectParser());
    registerParser(new ObjectCollectionParser());
    registerParser(new ObjectArrayParser());
    registerParser(new PrimitiveArrayParser());
    registerParser(new PrimitiveCollectionParser());
    registerParser(new PrimitiveParser());
  }

  private final Stack<Map<String, LabeledCollection>> labelsMapStack = new Stack<Map<String, LabeledCollection>>();
  private final int requiredLevel;

  public CodeTreeParser(int requiredLevel) {
    labelsMapStack.addAll(staticLabelsMapStack);
    this.requiredLevel = requiredLevel;
  }

  public static void registerLabeledCollection(LabeledCollection labeledCollection, String... ids) {
    Map<String, LabeledCollection> map = new HashMap<String, LabeledCollection>();
    for (String id : ids)
      map.put(id, labeledCollection);
    staticLabelsMapStack.push(map);
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

  static private void addLabelMaps(Map<String, LabeledCollection> labelsMap, final Method method, final Object container) {
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

  static private Map<String, LabeledCollection> buildLabelMaps(Object container) {
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

  private boolean isMonitored(ClassNode classNode, Field field) {
    if (field.isSynthetic() || field.isAnnotationPresent(IgnoreMonitor.class))
      return false;
    Monitor monitor = field.getDeclaringClass().getAnnotation(Monitor.class);
    if (field.isAnnotationPresent(Monitor.class))
      monitor = field.getAnnotation(Monitor.class);
    if (monitor == null)
      return false;
    return monitor.level() <= requiredLevel;
  }

  @Override
  public CodeNode recursiveParseInstance(MutableParentNode parentNode, Field fieldInstance, String instanceLabel,
      Object instance) {
    for (FieldParser parser : parsers) {
      if (parser.canParse(instance)) {
        return parser.parse(this, parentNode, fieldInstance, instanceLabel, instance);
      }
    }
    return null;
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
            recursiveParseInstance(classNode, field, null, fieldValue);
        }
      }
      objectClass = objectClass.getSuperclass();
    }
    if (container instanceof MonitorContainer)
      ((MonitorContainer) container).addToMonitor(new DataMonitor() {
        @Override
        public void add(String label, Monitored monitored) {
          PrimitiveNode child = new PrimitiveNode(label, classNode, monitored, classNode.level());
          classNode.addChild(child);
        }
      });
    if (container instanceof Monitored) {
      PrimitiveNode child = new PrimitiveNode("this", classNode, (Monitored) container, classNode.level());
      classNode.addChild(child);
    }
    labelsMapStack.pop();
  }

  @Override
  public CodeNode parse(MutableParentNode parent, Object root, String rootLabel) {
    return recursiveParseInstance(parent, null, rootLabel, root);
  }

  static public void registerParser(FieldParser parser) {
    parsers.addFirst(parser);
  }

  private LabeledCollection getLabeledElement(String id) {
    for (Map<String, LabeledCollection> labelsMap : labelsMapStack) {
      LabeledCollection labeledElement = labelsMap.get(id);
      if (labeledElement != null)
        return labeledElement;
    }
    return null;
  }

  @Override
  public CollectionLabelBuilder newCollectionLabelBuilder(Field field, int length) {
    String id = "";
    boolean arrayDecoration = true;
    if (field != null && field.isAnnotationPresent(Monitor.class)) {
      Monitor annotation = field.getAnnotation(Monitor.class);
      id = annotation.id();
      arrayDecoration = annotation.arrayDecoration();
    }
    if (field != null && id.isEmpty())
      id = field.getName();
    return new CollectionLabelBuilder(getLabeledElement(id), ":", length, arrayDecoration);
  }
}
