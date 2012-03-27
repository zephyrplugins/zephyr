package zephyr.plugin.core.api.internal.codeparser.interfaces;

public interface MutableParentNode extends ParentNode {
  void addChild(CodeNode codeNode);
}
