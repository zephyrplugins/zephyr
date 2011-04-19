package zephyr.plugin.core.api.codeparser.interfaces;

public interface MutableParentNode extends ParentNode {
  void addChild(CodeNode codeNode);
}
