package zephyr.plugin.core.api.codeparser.codetree;

import java.lang.reflect.Field;

import zephyr.plugin.core.api.codeparser.interfaces.ParentNode;


public class ObjectCollectionNode extends ClassNode {
  public ObjectCollectionNode(String label, ParentNode parent, Object instance, Field parentField) {
    super(label, parent, instance, parentField);
  }
}
