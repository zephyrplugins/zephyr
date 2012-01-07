package zephyr.plugin.core.internal.listeners;

import java.util.ArrayList;
import java.util.List;

import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.async.events.Event;
import zephyr.plugin.core.async.listeners.EventListener;
import zephyr.plugin.core.internal.ZephyrPluginCore;

public class PopupViewListener implements EventListener {
  static private List<CodeNode> registered = new ArrayList<CodeNode>();

  @Override
  public void listen(Event event) {
    while (!isEmpty()) {
      for (CodeNode codeNode : pullRegistred())
        ZephyrPluginCore.viewBinder().popup(codeNode);
    }
  }

  private List<CodeNode> pullRegistred() {
    List<CodeNode> result;
    synchronized (registered) {
      result = new ArrayList<CodeNode>(registered);
      registered.clear();
    }
    return result;
  }

  private boolean isEmpty() {
    synchronized (registered) {
      return registered.isEmpty();
    }
  }

  public static void register(CodeNode codeNode) {
    synchronized (registered) {
      registered.add(codeNode);
    }
  }
}
