package zephyr.plugin.core.api.codeparser.codetree;

import java.lang.reflect.Field;

import zephyr.plugin.core.api.codeparser.interfaces.ParentNode;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;


public class ClassNode extends AbstractParentNode {
  private final Object instance;
  private final Monitor classAnnotation;

  public ClassNode() {
    this("", null, null, null);
  }

  public ClassNode(String label, ParentNode parent, Object instance, Field parentField) {
    super(label, parent, CodeTrees.levelOf(parentField));
    this.instance = instance;
    classAnnotation = findClassAnnotation(instance);
  }

  protected Monitor findClassAnnotation(Object instance) {
    if (instance == null)
      return null;
    Class<?> classType = instance.getClass();
    do {
      Monitor monitor = classType.getAnnotation(Monitor.class);
      if (monitor != null)
        return monitor;
      classType = classType.getSuperclass();
    } while (classType != null);
    return null;
  }

  public boolean isClassAnnotated() {
    return classAnnotation != null;
  }

  public Object instance() {
    return instance;
  }
}
