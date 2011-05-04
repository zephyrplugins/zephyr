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
    classAnnotation = instance != null ? instance.getClass().getAnnotation(Monitor.class) : null;
  }

  public boolean isClassAnnotated() {
    return classAnnotation != null;
  }

  public Object instance() {
    return instance;
  }
}
