package zephyr.plugin.core.internal;

import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.codeparser.interfaces.PopupHandler;
import zephyr.plugin.core.api.codeparser.parsers.CodeTreeParser;
import zephyr.plugin.core.internal.listeners.PopupViewListener;

public class ZephyrCodeTreeParser extends CodeTreeParser implements PopupHandler {

  public ZephyrCodeTreeParser(int requiredLevel) {
    super(requiredLevel);
  }

  @Override
  public void popup(CodeNode codeNode) {
    PopupViewListener.register(codeNode);
  }
}
