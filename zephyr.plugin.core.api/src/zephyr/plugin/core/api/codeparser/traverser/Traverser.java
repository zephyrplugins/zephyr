package zephyr.plugin.core.api.codeparser.traverser;

import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;

public interface Traverser {
  boolean inNode(CodeNode codeNode);

  void outNode(CodeNode codeNode);
}
