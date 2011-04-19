package zephyr.plugin.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.codetree.ClockNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeParser;
import zephyr.plugin.core.api.codeparser.parsers.CodeTreeParser;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Clock;

public class SyncCode {
  public Signal<ClassNode> onParse = new Signal<ClassNode>();

  private final Map<Clock, ClockNode> clockNodes = new LinkedHashMap<Clock, ClockNode>();

  public SyncCode() {
  }

  public void parse(Clock clock, Object root) {
    ClockNode clockNode = clockNodes.get(clock);
    if (clockNode == null) {
      clockNode = new ClockNode(clock);
      clockNodes.put(clock, clockNode);
    }
    CodeParser parser = new CodeTreeParser();
    ClassNode rootClassNode = parser.parse(clockNode, root);
    clockNode.addChild(rootClassNode);
    onParse.fire(rootClassNode);
  }

  public List<ClockNode> clockNodes() {
    return new ArrayList<ClockNode>(clockNodes.values());
  }
}
