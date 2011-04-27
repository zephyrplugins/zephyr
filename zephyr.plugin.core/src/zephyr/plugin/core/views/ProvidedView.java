package zephyr.plugin.core.views;

import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;

public interface ProvidedView extends SyncView {
  boolean[] provide(CodeNode[] codeNodes);
}
