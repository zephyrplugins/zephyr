package zephyr.plugin.core.api.internal.codeparser.codetree;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.MutableParentNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.ParentNode;

public abstract class AbstractParentNode extends AbstractCodeNode implements MutableParentNode {
  private final Map<String, CodeNode> children = new LinkedHashMap<String, CodeNode>();

  protected AbstractParentNode(String label, ParentNode parent, int level) {
    super(label, parent, level);
  }

  @Override
  public void addChild(CodeNode child) {
    children.put(child.label(), child);
  }

  @Override
  public CodeNode getChild(int index) {
    Iterator<CodeNode> iterator = children.values().iterator();
    for (int i = 0; i < index; i++)
      iterator.next();
    return iterator.next();
  }

  @Override
  public int nbChildren() {
    return children.size();
  }

  @Override
  public CodeNode getChild(String id) {
    return children.get(id);
  }
}
