package zephyr.plugin.tests.codeparser.codetree;

import java.lang.reflect.Field;


public class CollectionNode extends ClassNode {
  public CollectionNode(String label, ParentNode parent, Object instance, Field parentField) {
    super(label, parent, instance, parentField);
  }

  @Override
  public String path() {
    return parent().path();
  }

  @Override
  public String uiLabel() {
    return String.format("%s[%d]", id(), nbChildren());
  }
}
