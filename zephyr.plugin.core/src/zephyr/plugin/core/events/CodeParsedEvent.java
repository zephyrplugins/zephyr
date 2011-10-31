package zephyr.plugin.core.events;

import zephyr.plugin.core.api.codeparser.codetree.ClockNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.async.events.Event;

public class CodeParsedEvent implements Event {
  public static final String ID = "zephyr.event.codeparsed";
  private final ClockNode clockNode;
  private final CodeNode node;

  public CodeParsedEvent(ClockNode clockNode, CodeNode node) {
    this.clockNode = clockNode;
    this.node = node;
  }

  @Override
  public String id() {
    return ID;
  }

  public CodeNode node() {
    return node;
  }

  public ClockNode clockNode() {
    return clockNode;
  }

  public Clock clock() {
    return clockNode.clock();
  }
}
