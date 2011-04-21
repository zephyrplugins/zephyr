package zephyr.plugin.core.views;

import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;

public interface DropTargetView extends SyncView {
  boolean isSupported(CodeNode codeNode);

  void drop(CodeNode[] supported);
}
