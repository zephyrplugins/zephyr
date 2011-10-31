package zephyr.plugin.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import zephyr.ZephyrCore;
import zephyr.plugin.core.api.codeparser.codetree.ClockNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeParser;
import zephyr.plugin.core.api.codeparser.parsers.CodeTreeParser;
import zephyr.plugin.core.api.monitoring.abstracts.MonitoredDataTraverser;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.events.CodeParsedEvent;

public class SyncCode {
  private final Map<Clock, ClockNode> clockNodes = new LinkedHashMap<Clock, ClockNode>();

  public SyncCode() {
  }

  public CodeNode parse(Clock clock, Object root) {
    ClockNode clockNode = clockNodes.get(clock);
    if (clockNode == null) {
      clockNode = new ClockNode(clock);
      clockNodes.put(clock, clockNode);
    }
    CodeParser parser = new CodeTreeParser(MonitoredDataTraverser.MonitorEverythingLevel);
    CodeNode newNode = parser.parse(clockNode, root);
    ZephyrCore.busEvent().dispatch(new CodeParsedEvent(clockNode, newNode));
    return newNode;
  }

  public List<ClockNode> getClockNodes() {
    return new ArrayList<ClockNode>(clockNodes.values());
  }

  public ClockNode clockNode(Clock clock) {
    return clockNodes.get(clock);
  }
}
