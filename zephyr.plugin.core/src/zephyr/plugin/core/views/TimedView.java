package zephyr.plugin.core.views;

import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;

public interface TimedView extends SyncView {
  boolean[] provide(CodeNode[] codeNode);
}
