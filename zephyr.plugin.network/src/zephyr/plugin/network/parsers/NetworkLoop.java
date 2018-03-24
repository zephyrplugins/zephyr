package zephyr.plugin.network.parsers;

import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;

@SuppressWarnings("restriction")
public interface NetworkLoop {
  void registerCodeNode(long id, CodeNode codeNode);

  Double getLastValue(long id);
}
