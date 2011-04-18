package zephyr.plugin.core.api.codeparser.codetree;

import java.lang.reflect.Field;


public class CollectionObjectNode extends ClassNode {
  public CollectionObjectNode(String label, ParentNode parent, Object instance, Field parentField) {
    super(label, parent, instance, parentField);
  }

  @Override
  public String[] path() {
    return parent().path();
  }
}
