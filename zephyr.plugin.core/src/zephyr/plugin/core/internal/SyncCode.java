package zephyr.plugin.core.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import zephyr.plugin.core.api.internal.codeparser.codetree.ClockNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeParser;
import zephyr.plugin.core.api.internal.codeparser.interfaces.ParentNode;
import zephyr.plugin.core.api.internal.monitoring.abstracts.MonitoredDataTraverser;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.events.CodeStructureEvent;
import zephyr.plugin.core.privates.ZephyrCodeTreeParser;
import zephyr.plugin.core.privates.ZephyrPluginCore;

public class SyncCode {
  private final Map<Clock, ClockNode> clockNodes = new LinkedHashMap<Clock, ClockNode>();

  public SyncCode() {
  }

  public CodeNode parse(Clock clock, Object root, String rootLabel) {
    ClockNode clockNode = clockNode(clock);
    CodeParser parser = new ZephyrCodeTreeParser(MonitoredDataTraverser.MonitorEverythingLevel);
    CodeNode newNode = parser.parse(clockNode, root, rootLabel);
    ZephyrSync.busEvent().dispatch(new CodeStructureEvent(CodeStructureEvent.ParsedID, clockNode, newNode));
    return newNode;
  }

  public List<ClockNode> getClockNodes() {
    return new ArrayList<ClockNode>(clockNodes.values());
  }

  public ClockNode clockNode(Clock clock) {
    ClockNode clockNode = clockNodes.get(clock);
    if (clockNode == null) {
      ZephyrPluginCore.clocks().register(clock);
      clockNode = new ClockNode(clock);
      clockNodes.put(clock, clockNode);
    }
    return clockNode;
  }

  public void removeClockNode(Clock clock) {
    ClockNode clockNode = clockNodes.remove(clock);
    ZephyrSync.busEvent().dispatch(new CodeStructureEvent(CodeStructureEvent.RemovedID, clockNode, clockNode));
  }

  public CodeNode findNode(String[] loadedPath) {
    for (ClockNode clockNode : clockNodes.values()) {
      if (!clockNode.label().equals(loadedPath[0]))
        continue;
      CodeNode found = findNode(clockNode, loadedPath);
      if (found != null)
        return found;
    }
    return null;
  }

  private static CodeNode findNode(ClockNode clockNode, String[] loadedPath) {
    if (loadedPath.length == 1)
      return clockNode;
    CodeNode currentNode = clockNode;
    for (int i = 1; i < loadedPath.length; i++) {
      if (currentNode == null)
        return null;
      if (!(currentNode instanceof ParentNode))
        return null;
      currentNode = ((ParentNode) currentNode).getChild(loadedPath[i]);
    }
    return currentNode;
  }
}
