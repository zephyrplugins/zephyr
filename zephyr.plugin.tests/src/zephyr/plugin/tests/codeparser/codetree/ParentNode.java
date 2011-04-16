package zephyr.plugin.tests.codeparser.codetree;

public interface ParentNode extends CodeNode {
  void addChild(CodeNode child);

  CodeNode getChild(int index);

  int nbChildren();
}
