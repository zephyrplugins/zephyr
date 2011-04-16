package zephyr.plugin.tests.codeparser.codetree;

import java.lang.reflect.Field;

import zephyr.plugin.core.api.monitoring.annotations.Monitor;


public class ClassNode extends AbstractParentNode {
  private final Object instance;
  private final Monitor classAnnotation;

  public ClassNode(String label, ParentNode parent, Object instance, Field parentField) {
    super(label, parent, parentField);
    this.instance = instance;
    classAnnotation = instance.getClass().getAnnotation(Monitor.class);
  }

  public boolean isClassAnnotated() {
    return classAnnotation != null;
  }

  public Object instance() {
    return instance;
  }

  @Override
  public String longLabel() {
    return String.format("%s %s", path(), String.valueOf(instance));
  }
}
