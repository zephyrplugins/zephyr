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
import zephyr.plugin.core.api.codeparser.codetree.CodeNode;
import zephyr.plugin.core.api.codeparser.codetree.ParentNode;
import zephyr.plugin.core.api.codeparser.traverser.Traverser;
import zephyr.plugin.core.api.monitoring.annotations.IgnoreMonitor;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.parsing.CollectionLabelBuilder;
import zephyr.plugin.core.api.parsing.LabelProvider;
import zephyr.plugin.core.api.parsing.LabeledElement;

public class CodeParser {
  static private LinkedList<Parser> parsers = new LinkedList<Parser>();
  private final Stack<Map<String, LabeledElement>> labelsMapStack = new Stack<Map<String, LabeledElement>>();

  static {
    registerParser(new ObjectParser());
    registerParser(new ObjectCollectionParser());
    registerParser(new ObjectArrayParser());
    registerParser(new PrimitiveArrayParser());
    registerParser(new PrimitiveListParser());
    registerParser(new PrimitiveParser());
  }

  static public Object getValueFromField(Field field, Object parentInstance) {
    try {
      return field.get(parentInstance);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
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
    if (field.isAnnotationPresent(IgnoreMonitor.class))
      return false;
    if (classNode.isClassAnnotated())
      return true;
    if (field.isAnnotationPresent(Monitor.class))
      return true;
    return false;
  }

  private void parseField(ClassNode parentNode, Field field) {
    Object fieldValue = getValueFromField(field, parentNode.instance());
    for (Parser parser : parsers) {
      if (parser.canParse(fieldValue)) {
        parser.parse(this, parentNode, field, fieldValue);
        return;
      }
    }
  }

  public void parseChildren(ClassNode classNode) {
    Object container = classNode.instance();
    labelsMapStack.push(buildLabelMaps(container));
    Class<?> objectClass = container.getClass();
    while (objectClass != null) {
      Field[] fields = getFieldList(objectClass);
      for (Field field : fields) {
        if (field.getName().equals("serialVersionUID"))
          continue;
        field.setAccessible(true);
        if (isMonitored(classNode, field))
          parseField(classNode, field);
      }
      objectClass = objectClass.getSuperclass();
    }
    labelsMapStack.pop();
  }

  public ClassNode parse(Object root) {
    return parse(null, root);
  }

  public ClassNode parse(ParentNode parent, Object root) {
    ClassNode classNode = new ClassNode("", parent, root, null);
    parseChildren(classNode);
    return classNode;
  }

  static public void registerParser(Parser parser) {
    parsers.addFirst(parser);
  }

  static public void traverse(Traverser traverser, CodeNode root) {
    boolean visitChildren = traverser.inNode(root);
    if (visitChildren && root instanceof ParentNode) {
      ParentNode parent = (ParentNode) root;
      for (int i = 0; i < parent.nbChildren(); i++)
        traverse(traverser, parent.getChild(i));
    }
    traverser.outNode(root);
  }

  private LabeledElement getLabeledElement(String id) {
    for (Map<String, LabeledElement> labelsMap : labelsMapStack) {
      LabeledElement labeledElement = labelsMap.get(id);
      if (labeledElement != null)
        return labeledElement;
    }
    return null;
  }

  public CollectionLabelBuilder newCollectionLabelBuilder(Field field, int length) {
    String id = "";
    boolean includeIndex = true;
    if (field.isAnnotationPresent(Monitor.class)) {
      Monitor annotation = field.getAnnotation(Monitor.class);
      id = annotation.id();
      includeIndex = annotation.arrayIndexLabeled();
    }
    if (id.isEmpty())
      id = field.getName();
    return new CollectionLabelBuilder(getLabeledElement(id), "", length, includeIndex);
  }
}
