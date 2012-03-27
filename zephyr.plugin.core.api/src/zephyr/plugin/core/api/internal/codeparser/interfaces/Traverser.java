package zephyr.plugin.core.api.internal.codeparser.interfaces;


public interface Traverser {
  boolean inNode(CodeNode codeNode);

  void outNode(CodeNode codeNode);
}
