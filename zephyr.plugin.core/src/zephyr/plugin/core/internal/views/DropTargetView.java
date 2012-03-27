package zephyr.plugin.core.internal.views;

import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;

public interface DropTargetView {
  boolean isSupported(CodeNode codeNode);

  void drop(CodeNode[] supported);
}
