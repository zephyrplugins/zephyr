package zephyr.plugin.core.api.codeparser.codetree;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractParentNode extends AbstractCodeNode implements ParentNode {
  private final List<CodeNode> children = new ArrayList<CodeNode>();

  protected AbstractParentNode(String label, ParentNode parent, Field parentField) {
    super(label, parent);
  }

  public void addChild(CodeNode child) {
    children.add(child);
  }

  @Override
  public CodeNode getChild(int index) {
    return children.get(index);
  }

  @Override
  public int nbChildren() {
    return children.size();
  }
}
