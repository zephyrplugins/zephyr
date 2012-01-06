package zephyr.plugin.core.api.codeparser.interfaces;

public interface ParentNode extends CodeNode {
  CodeNode getChild(int index);

  CodeNode getChild(String id);

  int nbChildren();
}
