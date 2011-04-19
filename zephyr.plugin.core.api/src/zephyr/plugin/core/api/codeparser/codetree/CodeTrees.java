package zephyr.plugin.core.api.codeparser.codetree;

import java.lang.reflect.Field;

import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.codeparser.interfaces.ParentNode;
import zephyr.plugin.core.api.codeparser.traverser.Traverser;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class CodeTrees {
  static private Class<?>[] primitives = { Double.class, Float.class, Byte.class, Boolean.class, Integer.class,
      Short.class };

  static public boolean isPrimitive(Class<? extends Object> fieldClass) {
    if (fieldClass.isPrimitive())
      return true;
    for (Class<?> c : primitives)
      if (c.equals(fieldClass))
        return true;
    return false;
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

  static public void traverse(Traverser traverser, CodeNode root) {
    boolean visitChildren = traverser.inNode(root);
    if (visitChildren && root instanceof ParentNode) {
      ParentNode parent = (ParentNode) root;
      for (int i = 0; i < parent.nbChildren(); i++)
        traverse(traverser, parent.getChild(i));
    }
    traverser.outNode(root);
  }

  public static String labelOf(Field field) {
    if (field == null)
      return "";
    Monitor annotation = field.getAnnotation(Monitor.class);
    if (annotation == null)
      return field.getName();
    String label = annotation.label();
    if (label.isEmpty() && !annotation.emptyLabel())
      label = field.getName();
    return label;
  }

  public static int levelOf(Field field) {
    if (field == null)
      return 0;
    Monitor annotation = field.getAnnotation(Monitor.class);
    if (annotation == null)
      return 0;
    return annotation.level();
  }
}
