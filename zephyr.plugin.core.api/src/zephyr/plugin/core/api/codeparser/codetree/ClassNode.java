package zephyr.plugin.core.api.codeparser.codetree;

import java.lang.reflect.Field;

import zephyr.plugin.core.api.codeparser.interfaces.ParentNode;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;


public class ClassNode extends AbstractParentNode {
  private final Object instance;
  private final Monitor fieldAnnotation;

  public ClassNode(String label, ParentNode parent, Object instance, Field parentField) {
    super(label, parent, CodeTrees.levelOf(parentField));
    this.instance = instance;
    fieldAnnotation = parentField != null ? parentField.getAnnotation(Monitor.class) : null;
  }

  public boolean isAdvertized() {
    return fieldAnnotation != null ? fieldAnnotation.advertize() : true;
  }

  public Object instance() {
    return instance;
  }
}
