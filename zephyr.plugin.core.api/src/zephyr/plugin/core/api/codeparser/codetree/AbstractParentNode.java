package zephyr.plugin.core.api.codeparser.codetree;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.codeparser.interfaces.MutableParentNode;
import zephyr.plugin.core.api.codeparser.interfaces.ParentNode;

public abstract class AbstractParentNode extends AbstractCodeNode implements MutableParentNode {
  private final List<CodeNode> children = new ArrayList<CodeNode>();

  protected AbstractParentNode(String label, ParentNode parent, Field parentField) {
    super(label, parent);
  }

  @Override
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
