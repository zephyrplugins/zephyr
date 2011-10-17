package zephyr.plugin.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import zephyr.plugin.core.api.codeparser.codetree.ClockNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeParser;
import zephyr.plugin.core.api.codeparser.parsers.CodeTreeParser;
import zephyr.plugin.core.api.monitoring.abstracts.MonitoredDataTraverser;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Clock;

public class SyncCode {
  public Signal<CodeNode> onParse = new Signal<CodeNode>();

  private final Map<Clock, ClockNode> clockNodes = new LinkedHashMap<Clock, ClockNode>();

  public SyncCode() {
  }

  public CodeNode[] parse(Clock clock, Object root) {
    ClockNode clockNode = clockNodes.get(clock);
    if (clockNode == null) {
      clockNode = new ClockNode(clock);
      clockNodes.put(clock, clockNode);
    }
    int nbChildrenBefore = clockNode.nbChildren();
    CodeParser parser = new CodeTreeParser(MonitoredDataTraverser.MonitorEverythingLevel);
    parser.parse(clockNode, root);
    CodeNode[] children = new CodeNode[clockNode.nbChildren() - nbChildrenBefore];
    for (int i = nbChildrenBefore; i < clockNode.nbChildren(); i++) {
      children[i - nbChildrenBefore] = clockNode.getChild(i);
      onParse.fire(children[i - nbChildrenBefore]);
    }
    return children;
  }

  public List<ClockNode> clockNodes() {
    return new ArrayList<ClockNode>(clockNodes.values());
  }

  public ClockNode clockNode(Clock clock) {
    return clockNodes.get(clock);
  }
}
