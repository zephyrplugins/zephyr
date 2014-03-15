package zephyr.plugin.core.api.internal.codeparser.codetree;

import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeHook;
import zephyr.plugin.core.api.internal.codeparser.interfaces.ParentNode;


public class ObjectCollectionNode extends ClassNode {
  public ObjectCollectionNode(String label, ParentNode parent, Object instance, CodeHook parentField) {
    super(label, parent, instance, parentField);
  }

  public Object children() {
    return null;
  }
}
