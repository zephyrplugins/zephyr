package zephyr.plugin.tests.codeparser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Clock;

public class CodeParser {
  public Signal<ClassNode> onParse = new Signal<ClassNode>();

  private final Map<Clock, ClockNode> clockNodes = new LinkedHashMap<Clock, ClockNode>();

  public CodeParser() {
  }

  public void parse(Clock clock, Object root) {
    ClockNode clockNode = clockNodes.get(clock);
    if (clockNode == null) {
      clockNode = new ClockNode(clock);
      clockNodes.put(clock, clockNode);
    }
    ClassNode rootClassNode = clockNode.addClassNode(root);
    onParse.fire(rootClassNode);
  }

  public List<ClockNode> clockNodes() {
    return new ArrayList<ClockNode>(clockNodes.values());
  }
}
