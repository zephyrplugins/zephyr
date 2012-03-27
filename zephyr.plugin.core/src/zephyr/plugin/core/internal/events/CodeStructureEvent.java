package zephyr.plugin.core.internal.events;

import zephyr.plugin.core.api.internal.codeparser.codetree.ClockNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.async.events.Event;

public class CodeStructureEvent implements Event {
  public static final String ParsedID = "zephyr.event.codeparsed";
  public static final String RemovedID = "zephyr.event.coderemoved";
  private final ClockNode clockNode;
  private final CodeNode node;
  private final String id;

  public CodeStructureEvent(String id, ClockNode clockNode, CodeNode node) {
    this.id = id;
    this.clockNode = clockNode;
    this.node = node;
  }

  @Override
  public String id() {
    return id;
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
