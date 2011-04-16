package zephyr.plugin.tests.codeparser.codetree;

import java.lang.reflect.Field;

public class PrimitiveNode extends AbstractCodeNode {
  public PrimitiveNode(String label, ParentNode parent, Field parentField) {
    super(label, parent, parentField);
  }
}
