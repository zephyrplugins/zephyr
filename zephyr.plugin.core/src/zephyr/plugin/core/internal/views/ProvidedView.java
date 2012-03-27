package zephyr.plugin.core.internal.views;

import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;

public interface ProvidedView extends SyncView {
  boolean[] provide(CodeNode[] codeNodes);
}
