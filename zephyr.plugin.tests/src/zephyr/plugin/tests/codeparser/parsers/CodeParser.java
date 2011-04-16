package zephyr.plugin.tests.codeparser.parsers;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

import zephyr.plugin.core.api.monitoring.annotations.IgnoreMonitor;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.tests.codeparser.codetree.ClassNode;
import zephyr.plugin.tests.codeparser.codetree.ClockNode;

public class CodeParser {
  static private LinkedList<Parser> parsers = new LinkedList<Parser>();

  static {
    registerParser(new ObjectParser());
    registerParser(new ObjectCollectionParser());
    registerParser(new ObjectArrayParser());
    registerParser(new PrimitiveArrayParser());
    registerParser(new PrimitiveCollectionParser());
    registerParser(new PrimitiveParser());
  }

  private final ClockNode clockNode;

  public CodeParser(ClockNode clockNode) {
    this.clockNode = clockNode;
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
    Class<?> objectClass = container.getClass();
    while (objectClass != null) {
      Field[] fields = getFieldList(objectClass);
      for (Field field : fields) {
        if (field.getName().equals("serialVersionUID"))
          continue;
        boolean isAccessible = field.isAccessible();
        field.setAccessible(true);
        if (isMonitored(classNode, field))
          parseField(classNode, field);
        field.setAccessible(isAccessible);
      }
      objectClass = objectClass.getSuperclass();
    }
  }

  public ClassNode parse(Object root) {
    String label = root.getClass().getSimpleName();
    ClassNode classNode = new ClassNode(label, clockNode, root, null);
    parseChildren(classNode);
    return classNode;
  }

  static public void registerParser(Parser parser) {
    parsers.addFirst(parser);
  }
}
