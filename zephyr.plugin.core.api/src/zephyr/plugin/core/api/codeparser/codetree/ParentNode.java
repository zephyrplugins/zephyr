package zephyr.plugin.core.api.codeparser.codetree;

public interface ParentNode extends CodeNode {
  CodeNode getChild(int index);

  int nbChildren();
}
