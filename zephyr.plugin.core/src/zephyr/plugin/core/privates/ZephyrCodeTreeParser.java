package zephyr.plugin.core.privates;

import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.PopupHandler;
import zephyr.plugin.core.api.internal.codeparser.parsers.CodeTreeParser;
import zephyr.plugin.core.privates.listeners.PopupViewListener;

public class ZephyrCodeTreeParser extends CodeTreeParser implements PopupHandler {

  public ZephyrCodeTreeParser(int requiredLevel) {
    super(requiredLevel);
  }

  @Override
  public void popup(CodeNode codeNode) {
    PopupViewListener.register(codeNode);
  }
}
